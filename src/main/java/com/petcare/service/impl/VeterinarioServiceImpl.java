package com.petcare.service.impl;

import com.petcare.dto.VeterinarioDTO;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.exception.RegraNegocioException;
import com.petcare.model.Especie;
import com.petcare.model.Veterinario;
import com.petcare.repository.VeterinarioRepository;
import com.petcare.service.VeterinarioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository repository;

    public VeterinarioServiceImpl(VeterinarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public VeterinarioDTO.Response criar(VeterinarioDTO.Request req) {
        String crmv = req.crmv().trim().toUpperCase();
        if (repository.existsByCrmv(crmv)) {
            throw new RegraNegocioException("Já existe um veterinário cadastrado com este CRMV", HttpStatus.CONFLICT);
        }
        Veterinario vet = repository.save(new Veterinario(req.nome().trim(), crmv, req.especies()));
        return VeterinarioDTO.Response.de(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioDTO.Response> listar(Especie especie) {
        List<Veterinario> vets = (especie == null)
                ? repository.findAll()
                : repository.buscarHabilitadosParaEspecie(especie);
        return vets.stream().map(VeterinarioDTO.Response::de).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioDTO.Response buscar(Long id) {
        return VeterinarioDTO.Response.de(buscarEntidade(id));
    }

    @Override
    public Veterinario buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário", id));
    }
}
