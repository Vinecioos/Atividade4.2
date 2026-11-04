package com.petcare.dto;

import com.petcare.model.Atendimento;
import com.petcare.model.Consulta;
import com.petcare.model.Especie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class AtendimentoDTO {

    private AtendimentoDTO() {
    }

    public record Request(
            @NotNull(message = "Consulta é obrigatória") Long consultaId,
            @NotBlank(message = "Diagnóstico é obrigatório") @Size(max = 2000) String diagnostico,
            @Size(max = 2000) String observacoes,
            @Size(max = 2000) String orientacoes) {
    }

    public record Response(Long id, Long consultaId,
                           Long animalId, String animalNome, Especie especie, String tutorNome,
                           String veterinarioNome, String crmv, String local, LocalDateTime dataConsulta,
                           String diagnostico, String observacoes, String orientacoes,
                           LocalDateTime registradoEm) {
        public static Response de(Atendimento a) {
            Consulta c = a.getConsulta();
            return new Response(a.getId(), c.getId(),
                    c.getAnimal().getId(), c.getAnimal().getNome(), c.getAnimal().getEspecie(),
                    c.getAnimal().getTutor().getNome(),
                    c.getVeterinario().getNome(), c.getVeterinario().getCrmv(),
                    c.getLocal(), c.getDataHora(),
                    a.getDiagnostico(), a.getObservacoes(), a.getOrientacoes(), a.getRegistradoEm());
        }
    }
}
