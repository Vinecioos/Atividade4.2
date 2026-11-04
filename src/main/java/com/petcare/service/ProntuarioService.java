package com.petcare.service;

import com.petcare.dto.AtendimentoDTO;
import com.petcare.dto.HistoricoDTO;
import com.petcare.dto.VacinacaoDTO;

import java.util.List;

/** Prontuário do animal: atendimentos, vacinas e histórico. */
public interface ProntuarioService {

    AtendimentoDTO.Response registrarAtendimento(AtendimentoDTO.Request req);

    AtendimentoDTO.Response buscarAtendimento(Long id);

    List<AtendimentoDTO.Response> atendimentosDoAnimal(Long animalId);

    VacinacaoDTO.Response registrarVacina(VacinacaoDTO.Request req);

    /** @param nomeVacina filtro opcional (busca parcial). */
    List<VacinacaoDTO.Response> vacinasDoAnimal(Long animalId, String nomeVacina);

    HistoricoDTO.Response historico(Long animalId);
}
