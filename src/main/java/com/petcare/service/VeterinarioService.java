package com.petcare.service;

import com.petcare.dto.VeterinarioDTO;
import com.petcare.model.Especie;
import com.petcare.model.Veterinario;

import java.util.List;

public interface VeterinarioService {

    VeterinarioDTO.Response criar(VeterinarioDTO.Request req);

    /** @param especie filtro opcional: apenas veterinários habilitados para a espécie. */
    List<VeterinarioDTO.Response> listar(Especie especie);

    VeterinarioDTO.Response buscar(Long id);

    Veterinario buscarEntidade(Long id);
}
