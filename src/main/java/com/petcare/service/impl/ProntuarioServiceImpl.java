package com.petcare.service.impl;

import com.petcare.dto.AnimalDTO;
import com.petcare.dto.AtendimentoDTO;
import com.petcare.dto.HistoricoDTO;
import com.petcare.dto.VacinacaoDTO;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.exception.RegraNegocioException;
import com.petcare.model.Animal;
import com.petcare.model.Atendimento;
import com.petcare.model.Consulta;
import com.petcare.model.StatusConsulta;
import com.petcare.model.Vacinacao;
import com.petcare.repository.AtendimentoRepository;
import com.petcare.repository.VacinacaoRepository;
import com.petcare.service.AnimalService;
import com.petcare.service.ConsultaService;
import com.petcare.service.ProntuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProntuarioServiceImpl implements ProntuarioService {

    private final AtendimentoRepository atendimentoRepository;
    private final VacinacaoRepository vacinacaoRepository;
    private final ConsultaService consultaService;
    private final AnimalService animalService;

    public ProntuarioServiceImpl(AtendimentoRepository atendimentoRepository,
                                 VacinacaoRepository vacinacaoRepository,
                                 ConsultaService consultaService,
                                 AnimalService animalService) {
        this.atendimentoRepository = atendimentoRepository;
        this.vacinacaoRepository = vacinacaoRepository;
        this.consultaService = consultaService;
        this.animalService = animalService;
    }

    // ---------- Atendimentos ----------

    @Override
    @Transactional
    public AtendimentoDTO.Response registrarAtendimento(AtendimentoDTO.Request req) {
        Consulta consulta = consultaService.buscarEntidade(req.consultaId());

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RegraNegocioException("Não é possível registrar atendimento de uma consulta cancelada",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (consulta.getStatus() == StatusConsulta.REALIZADA
                || atendimentoRepository.existsByConsultaId(consulta.getId())) {
            throw new RegraNegocioException("Esta consulta já possui atendimento registrado", HttpStatus.CONFLICT);
        }

        Atendimento atendimento = atendimentoRepository.save(new Atendimento(consulta,
                req.diagnostico().trim(), limpar(req.observacoes()), limpar(req.orientacoes())));
        consulta.marcarRealizada();
        return AtendimentoDTO.Response.de(atendimento);
    }

    @Override
    @Transactional(readOnly = true)
    public AtendimentoDTO.Response buscarAtendimento(Long id) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento", id));
        return AtendimentoDTO.Response.de(atendimento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtendimentoDTO.Response> atendimentosDoAnimal(Long animalId) {
        animalService.buscarEntidade(animalId); // garante 404 se o animal não existir
        return atendimentoRepository.findByConsultaAnimalIdOrderByRegistradoEmDesc(animalId).stream()
                .map(AtendimentoDTO.Response::de).toList();
    }

    // ---------- Vacinação ----------

    @Override
    @Transactional
    public VacinacaoDTO.Response registrarVacina(VacinacaoDTO.Request req) {
        Animal animal = animalService.buscarEntidade(req.animalId());
        Vacinacao vacina = vacinacaoRepository.save(new Vacinacao(animal, req.nomeVacina().trim(),
                req.dataAplicacao(), req.lote().trim()));
        return VacinacaoDTO.Response.de(vacina);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacinacaoDTO.Response> vacinasDoAnimal(Long animalId, String nomeVacina) {
        animalService.buscarEntidade(animalId);
        List<Vacinacao> vacinas = (nomeVacina == null || nomeVacina.isBlank())
                ? vacinacaoRepository.findByAnimalIdOrderByDataAplicacaoDesc(animalId)
                : vacinacaoRepository.buscarPorNomeDaVacina(animalId, nomeVacina.trim());
        return vacinas.stream().map(VacinacaoDTO.Response::de).toList();
    }

    // ---------- Histórico completo ----------

    @Override
    @Transactional(readOnly = true)
    public HistoricoDTO.Response historico(Long animalId) {
        Animal animal = animalService.buscarEntidade(animalId);
        return new HistoricoDTO.Response(
                AnimalDTO.Response.de(animal),
                atendimentoRepository.findByConsultaAnimalIdOrderByRegistradoEmDesc(animalId).stream()
                        .map(AtendimentoDTO.Response::de).toList(),
                vacinacaoRepository.findByAnimalIdOrderByDataAplicacaoDesc(animalId).stream()
                        .map(VacinacaoDTO.Response::de).toList());
    }

    private String limpar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
