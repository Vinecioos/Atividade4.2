package com.petcare.service.impl;

import com.petcare.dto.AnimalDTO;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.model.Animal;
import com.petcare.model.Especie;
import com.petcare.model.Tutor;
import com.petcare.repository.AnimalRepository;
import com.petcare.service.AnimalService;
import com.petcare.service.TutorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository repository;
    private final TutorService tutorService;

    public AnimalServiceImpl(AnimalRepository repository, TutorService tutorService) {
        this.repository = repository;
        this.tutorService = tutorService;
    }

    @Override
    @Transactional
    public AnimalDTO.Response criar(AnimalDTO.Request req) {
        Tutor tutor = tutorService.buscarEntidade(req.tutorId());
        Animal animal = repository.save(new Animal(req.nome().trim(), req.especie(),
                req.raca().trim(), req.dataNascimento(), tutor));
        return AnimalDTO.Response.de(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalDTO.Response> listar(Long tutorId, Especie especie) {
        List<Animal> animais;
        if (tutorId != null) {
            animais = repository.findByTutorIdOrderByNome(tutorId).stream()
                    .filter(a -> especie == null || a.getEspecie() == especie)
                    .toList();
        } else if (especie != null) {
            animais = repository.buscarPorEspecieComTutor(especie);
        } else {
            animais = repository.findAllByOrderByNome();
        }
        return animais.stream().map(AnimalDTO.Response::de).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalDTO.Response buscar(Long id) {
        return AnimalDTO.Response.de(buscarEntidade(id));
    }

    @Override
    public Animal buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Animal", id));
    }
}
