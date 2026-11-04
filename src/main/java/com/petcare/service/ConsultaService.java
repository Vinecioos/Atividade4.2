package com.petcare.service;

import com.petcare.dto.ConsultaDTO;
import com.petcare.model.Consulta;
import com.petcare.model.StatusConsulta;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaService {

    /** Aplica as regras: espécie habilitada e horário livre para o veterinário. */
    ConsultaDTO.Response agendar(ConsultaDTO.Request req);

    List<ConsultaDTO.Response> listar(Long animalId, Long veterinarioId, StatusConsulta status);

    ConsultaDTO.Response buscar(Long id);

    /** Cancela uma consulta AGENDADA e libera o horário. */
    ConsultaDTO.Response cancelar(Long id);

    /** Consultas AGENDADAS do veterinário no período [inicio, fim). */
    List<ConsultaDTO.Response> agendaDoVeterinario(Long veterinarioId, LocalDateTime inicio, LocalDateTime fim);

    Consulta buscarEntidade(Long id);
}
