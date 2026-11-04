package com.petcare;

import com.jayway.jsonpath.JsonPath;
import com.petcare.util.CpfUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetCareApiTests {

    @Autowired
    MockMvc mvc;

    private Long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private void postEsperando(String url, String json, int statusEsperado) throws Exception {
        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(statusEsperado));
    }

    private String consultaJson(Long animalId, Long vetId, LocalDateTime dataHora) {
        return """
                {"animalId": %d, "veterinarioId": %d, "local": "Consultório 1", "dataHora": "%s"}
                """.formatted(animalId, vetId, dataHora);
    }

    @Test
    void validaCpf() {
        assertTrue(CpfUtil.valido("52998224725"));
        assertFalse(CpfUtil.valido("11111111111"));
        assertFalse(CpfUtil.valido("52998224726"));
    }

    @Test
    void fluxoCompleto() throws Exception {
        // ---------- Módulo 1: tutores e animais ----------
        Long tutorId = criar("/tutores", """
                {"nome": "Maria Silva", "cpf": "529.982.247-25", "telefone": "15 99999-0000",
                 "endereco": "Rua das Flores, 100"}
                """);
        postEsperando("/tutores", """
                {"nome": "Outra Pessoa", "cpf": "52998224725", "telefone": "1", "endereco": "x"}
                """, 409); // CPF duplicado
        postEsperando("/tutores", """
                {"nome": "CPF Ruim", "cpf": "123", "telefone": "1", "endereco": "x"}
                """, 400); // CPF inválido
        postEsperando("/tutores", "{}", 400); // campos obrigatórios

        Long rexId = criar("/animais", """
                {"nome": "Rex", "especie": "CAO", "raca": "Labrador", "dataNascimento": "2020-03-10",
                 "tutorId": %d}
                """.formatted(tutorId));
        Long mimiId = criar("/animais", """
                {"nome": "Mimi", "especie": "GATO", "raca": "Siamês", "dataNascimento": "2021-07-01",
                 "tutorId": %d}
                """.formatted(tutorId));
        postEsperando("/animais", """
                {"nome": "Fantasma", "especie": "CAO", "raca": "SRD", "dataNascimento": "2020-01-01",
                 "tutorId": 99999}
                """, 404); // tutor inexistente

        mvc.perform(get("/tutores/" + tutorId + "/animais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // 1 tutor : N animais

        // ---------- Módulo 2: veterinários e consultas ----------
        Long vetGatos = criar("/veterinarios", """
                {"nome": "Dra. Ana", "crmv": "SP-11111", "especies": ["GATO"]}
                """);
        Long vetCaesGatos = criar("/veterinarios", """
                {"nome": "Dr. Bruno", "crmv": "SP-22222", "especies": ["CAO", "GATO"]}
                """);
        postEsperando("/veterinarios", """
                {"nome": "Clone", "crmv": "sp-11111", "especies": ["GATO"]}
                """, 409); // CRMV duplicado (case-insensitive)

        LocalDateTime horario = LocalDateTime.now().plusDays(7)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);

        // veterinário não habilitado para a espécie
        postEsperando("/consultas", consultaJson(rexId, vetGatos, horario), 422);
        // data no passado
        postEsperando("/consultas", consultaJson(rexId, vetCaesGatos, LocalDateTime.now().minusDays(1)), 400);

        Long consulta1 = criar("/consultas", consultaJson(rexId, vetCaesGatos, horario));

        // mesmo veterinário, mesmo horário -> conflito
        postEsperando("/consultas", consultaJson(mimiId, vetCaesGatos, horario), 409);
        // outro veterinário no mesmo horário -> permitido
        Long consultaOutroVet = criar("/consultas", consultaJson(mimiId, vetGatos, horario));

        // consultar
        mvc.perform(get("/consultas/" + consulta1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGENDADA"));
        mvc.perform(get("/consultas").param("veterinarioId", vetCaesGatos.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // cancelar libera o horário
        mvc.perform(patch("/consultas/" + consulta1 + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
        mvc.perform(patch("/consultas/" + consulta1 + "/cancelar"))
                .andExpect(status().isConflict()); // já cancelada
        Long consultaRebook = criar("/consultas", consultaJson(mimiId, vetCaesGatos, horario));

        // ---------- Módulo 3: prontuário e vacinação ----------
        postEsperando("/atendimentos", """
                {"consultaId": %d, "diagnostico": "x"}
                """.formatted(consulta1), 422); // consulta cancelada

        String atendimento = mvc.perform(post("/atendimentos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"consultaId": %d, "diagnostico": "Otite leve <b>no ouvido</b>",
                                 "observacoes": "Animal agitado\\nSem febre",
                                 "orientacoes": "Limpeza diária e retorno em 10 dias"}
                                """.formatted(consultaRebook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.animalNome").value("Mimi"))
                .andReturn().getResponse().getContentAsString();
        Long atendimentoId = ((Number) JsonPath.read(atendimento, "$.id")).longValue();

        postEsperando("/atendimentos", """
                {"consultaId": %d, "diagnostico": "repetido"}
                """.formatted(consultaRebook), 409); // já atendida

        mvc.perform(get("/consultas/" + consultaRebook))
                .andExpect(jsonPath("$.status").value("REALIZADA"));

        criar("/vacinacoes", """
                {"animalId": %d, "nomeVacina": "V10", "dataAplicacao": "2026-01-15", "lote": "L-2026-77"}
                """.formatted(mimiId));
        postEsperando("/vacinacoes", """
                {"animalId": %d, "nomeVacina": "Raiva", "dataAplicacao": "2999-01-01", "lote": "L1"}
                """.formatted(mimiId), 400); // data futura

        mvc.perform(get("/animais/" + mimiId + "/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animal.nome").value("Mimi"))
                .andExpect(jsonPath("$.atendimentos.length()").value(1))
                .andExpect(jsonPath("$.vacinas.length()").value(1))
                .andExpect(jsonPath("$.vacinas[0].lote").value("L-2026-77"));
        mvc.perform(get("/animais/" + rexId + "/historico"))
                .andExpect(jsonPath("$.atendimentos.length()").value(0))
                .andExpect(jsonPath("$.vacinas.length()").value(0));

        // impressão: HTML com texto do usuário escapado
        String html = mvc.perform(get("/atendimentos/" + atendimentoId + "/imprimir"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn().getResponse().getContentAsString();
        assertTrue(html.contains("Mimi"));
        assertTrue(html.contains("Otite leve &lt;b&gt;no ouvido&lt;/b&gt;"));
        assertFalse(html.contains("<b>no ouvido</b>"));

        // ---------- Consultas personalizadas (@Query) ----------
        mvc.perform(get("/tutores").param("nome", "mar")) // buscarPorNome
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Maria Silva"));
        mvc.perform(get("/animais").param("especie", "GATO")) // buscarPorEspecieComTutor
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Mimi"));
        mvc.perform(get("/veterinarios").param("especie", "CAO")) // buscarHabilitadosParaEspecie
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Dr. Bruno"));
        mvc.perform(get("/veterinarios/" + vetCaesGatos + "/agenda") // buscarAgenda
                        .param("inicio", horario.toLocalDate().atStartOfDay().toString())
                        .param("fim", horario.toLocalDate().plusDays(1).atStartOfDay().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // a consulta do horário já foi REALIZADA
        mvc.perform(get("/animais/" + mimiId + "/vacinas").param("nome", "v1")) // buscarPorNomeDaVacina
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/animais/" + mimiId + "/vacinas").param("nome", "raiva"))
                .andExpect(jsonPath("$.length()").value(0));

        mvc.perform(get("/atendimentos/99999")).andExpect(status().isNotFound());
        mvc.perform(get("/consultas").param("status", "INVALIDO")).andExpect(status().isBadRequest());
    }
}
