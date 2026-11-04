package com.petcare.service;

import com.petcare.dto.TutorDTO;
import com.petcare.model.Tutor;

import java.util.List;

public interface TutorService {

    TutorDTO.Response criar(TutorDTO.Request req);

    TutorDTO.Response atualizar(Long id, TutorDTO.Request req);

    /** @param nome filtro opcional (busca parcial); nulo ou vazio lista todos. */
    List<TutorDTO.Response> listar(String nome);

    TutorDTO.Response buscar(Long id);

    /** Uso interno entre serviços. Lança 404 se não existir. */
    Tutor buscarEntidade(Long id);
}
