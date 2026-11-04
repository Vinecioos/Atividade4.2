package com.petcare.controller;

import com.petcare.dto.AnimalDTO;
import com.petcare.dto.TutorDTO;
import com.petcare.service.AnimalService;
import com.petcare.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService service;
    private final AnimalService animalService;

    public TutorController(TutorService service, AnimalService animalService) {
        this.service = service;
        this.animalService = animalService;
    }

    @PostMapping
    public ResponseEntity<TutorDTO.Response> criar(@Valid @RequestBody TutorDTO.Request req) {
        TutorDTO.Response criado = service.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    /** Filtro opcional: ?nome=mar (busca parcial, sem diferenciar maiúsculas). */
    @GetMapping
    public List<TutorDTO.Response> listar(@RequestParam(required = false) String nome) {
        return service.listar(nome);
    }

    @GetMapping("/{id}")
    public TutorDTO.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public TutorDTO.Response atualizar(@PathVariable Long id, @Valid @RequestBody TutorDTO.Request req) {
        return service.atualizar(id, req);
    }

    /** Lista os animais de um tutor (relacionamento 1:N). */
    @GetMapping("/{id}/animais")
    public List<AnimalDTO.Response> animaisDoTutor(@PathVariable Long id) {
        service.buscar(id); // 404 se o tutor não existir
        return animalService.listar(id, null);
    }
}
