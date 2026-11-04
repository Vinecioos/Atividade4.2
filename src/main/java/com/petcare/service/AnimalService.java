package com.petcare.service;

import com.petcare.dto.AnimalDTO;
import com.petcare.model.Animal;
import com.petcare.model.Especie;

import java.util.List;

public interface AnimalService {

    AnimalDTO.Response criar(AnimalDTO.Request req);

    /** Filtros opcionais: tutor e/ou espécie. */
    List<AnimalDTO.Response> listar(Long tutorId, Especie especie);

    AnimalDTO.Response buscar(Long id);

    Animal buscarEntidade(Long id);
}
