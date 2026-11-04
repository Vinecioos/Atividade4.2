package com.petcare.service.impl;

import com.petcare.dto.ConsultaDTO;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.exception.RegraNegocioException;
import com.petcare.model.Animal;
import com.petcare.model.Consulta;
import com.petcare.model.StatusConsulta;
import com.petcare.model.Veterinario;
import com.petcare.repository.ConsultaRepository;
import com.petcare.service.AnimalService;
import com.petcare.service.ConsultaService;
import com.petcare.service.VeterinarioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ConsultaServiceImpl implements ConsultaService {

    private final ConsultaRepository repository;
    private final AnimalService animalService;
    private final VeterinarioService veterinarioService;

    public ConsultaServiceImpl(ConsultaRepository repository, AnimalService animalService,
                               VeterinarioService veterinarioService) {
        this.repository = repository;
        this.animalService = animalService;
        this.veterinarioService = veterinarioService;
    }

    @Override
    @Transactional
    public ConsultaDTO.Response agendar(ConsultaDTO.Request req) {
        Animal animal = animalService.buscarEntidade(req.animalId());
        Veterinario vet = veterinarioService.buscarEntidade(req.veterinarioId());

        // Regra 1: veterinário habilitado para a espécie do animal
        if (!vet.atende(animal.getEspecie())) {
            throw new RegraNegocioException("O veterinário " + vet.getNome()
                    + " não está habilitado para atender a espécie " + animal.getEspecie(),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // Regra 2: o veterinário não pode ter duas consultas no mesmo horário
        LocalDateTime dataHora = req.dataHora().truncatedTo(ChronoUnit.MINUTES);
        if (repository.existsByChaveAgenda(Consulta.gerarChave(vet.getId(), dataHora))) {
            throw new RegraNegocioException("O veterinário " + vet.getNome()
                    + " já possui uma consulta neste horário", HttpStatus.CONFLICT);
        }

        // saveAndFlush: se duas requisições simultâneas passarem pela checagem acima,
        // a restrição UNIQUE do banco barra a segunda (tratada como 409 no handler).
        Consulta consulta = repository.saveAndFlush(new Consulta(animal, vet, req.local().trim(), dataHora));
        return ConsultaDTO.Response.de(consulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaDTO.Response> listar(Long animalId, Long veterinarioId, StatusConsulta status) {
        List<Consulta> base;
        if (animalId != null) {
            base = repository.findByAnimalIdOrderByDataHora(animalId);
        } else if (veterinarioId != null) {
            base = repository.findByVeterinarioIdOrderByDataHora(veterinarioId);
        } else {
            base = repository.findAllByOrderByDataHora();
        }
        return base.stream()
                .filter(c -> veterinarioId == null || c.getVeterinario().getId().equals(veterinarioId))
                .filter(c -> status == null || c.getStatus() == status)
                .map(ConsultaDTO.Response::de)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultaDTO.Response buscar(Long id) {
        return ConsultaDTO.Response.de(buscarEntidade(id));
    }

    @Override
    @Transactional
    public ConsultaDTO.Response cancelar(Long id) {
        Consulta consulta = buscarEntidade(id);
        if (consulta.getStatus() != StatusConsulta.AGENDADA) {
            throw new RegraNegocioException("Somente consultas com status AGENDADA podem ser canceladas "
                    + "(status atual: " + consulta.getStatus() + ")", HttpStatus.CONFLICT);
        }
        consulta.cancelar();
        return ConsultaDTO.Response.de(consulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaDTO.Response> agendaDoVeterinario(Long veterinarioId, LocalDateTime inicio,
                                                          LocalDateTime fim) {
        veterinarioService.buscarEntidade(veterinarioId); // 404 se não existir
        if (!fim.isAfter(inicio)) {
            throw new RegraNegocioException("O fim do período deve ser posterior ao início",
                    HttpStatus.BAD_REQUEST);
        }
        return repository.buscarAgenda(veterinarioId, StatusConsulta.AGENDADA, inicio, fim).stream()
                .map(ConsultaDTO.Response::de).toList();
    }

    @Override
    public Consulta buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", id));
    }
}
