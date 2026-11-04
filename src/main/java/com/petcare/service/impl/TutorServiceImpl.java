package com.petcare.service.impl;

import com.petcare.dto.TutorDTO;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.exception.RegraNegocioException;
import com.petcare.model.Tutor;
import com.petcare.repository.TutorRepository;
import com.petcare.service.TutorService;
import com.petcare.util.CpfUtil;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TutorServiceImpl implements TutorService {

    private final TutorRepository repository;

    public TutorServiceImpl(TutorRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TutorDTO.Response criar(TutorDTO.Request req) {
        String cpf = validarCpf(req.cpf());
        if (repository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um tutor cadastrado com este CPF", HttpStatus.CONFLICT);
        }
        Tutor tutor = repository.save(new Tutor(req.nome().trim(), cpf, req.telefone().trim(), req.endereco().trim()));
        return TutorDTO.Response.de(tutor);
    }

    @Override
    @Transactional
    public TutorDTO.Response atualizar(Long id, TutorDTO.Request req) {
        Tutor tutor = buscarEntidade(id);
        String cpf = validarCpf(req.cpf());
        if (!cpf.equals(tutor.getCpf()) && repository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um tutor cadastrado com este CPF", HttpStatus.CONFLICT);
        }
        tutor.setNome(req.nome().trim());
        tutor.setCpf(cpf);
        tutor.setTelefone(req.telefone().trim());
        tutor.setEndereco(req.endereco().trim());
        return TutorDTO.Response.de(tutor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorDTO.Response> listar(String nome) {
        List<Tutor> tutores = (nome == null || nome.isBlank())
                ? repository.findAll(Sort.by("nome"))
                : repository.buscarPorNome(nome.trim());
        return tutores.stream().map(TutorDTO.Response::de).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TutorDTO.Response buscar(Long id) {
        return TutorDTO.Response.de(buscarEntidade(id));
    }

    @Override
    public Tutor buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Tutor", id));
    }

    private String validarCpf(String cpfInformado) {
        String cpf = CpfUtil.normalizar(cpfInformado);
        if (!CpfUtil.valido(cpf)) {
            throw new RegraNegocioException("CPF inválido", HttpStatus.BAD_REQUEST);
        }
        return cpf;
    }
}
