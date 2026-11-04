package com.petcare.controller;

import com.petcare.dto.AnimalDTO;
import com.petcare.dto.AtendimentoDTO;
import com.petcare.dto.HistoricoDTO;
import com.petcare.dto.VacinacaoDTO;
import com.petcare.model.Especie;
import com.petcare.service.AnimalService;
import com.petcare.service.ProntuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService service;
    private final ProntuarioService prontuarioService;

    public AnimalController(AnimalService service, ProntuarioService prontuarioService) {
        this.service = service;
        this.prontuarioService = prontuarioService;
    }

    @PostMapping
    public ResponseEntity<AnimalDTO.Response> criar(@Valid @RequestBody AnimalDTO.Request req) {
        AnimalDTO.Response criado = service.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    /** Filtros opcionais: ?tutorId=1&especie=CAO */
    @GetMapping
    public List<AnimalDTO.Response> listar(@RequestParam(required = false) Long tutorId,
                                           @RequestParam(required = false) Especie especie) {
        return service.listar(tutorId, especie);
    }

    @GetMapping("/{id}")
    public AnimalDTO.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    // ----- Módulo 3: histórico do animal -----

    @GetMapping("/{id}/atendimentos")
    public List<AtendimentoDTO.Response> atendimentos(@PathVariable Long id) {
        return prontuarioService.atendimentosDoAnimal(id);
    }

    /** Filtro opcional: ?nome=v10 (busca parcial pelo nome da vacina). */
    @GetMapping("/{id}/vacinas")
    public List<VacinacaoDTO.Response> vacinas(@PathVariable Long id,
                                               @RequestParam(required = false) String nome) {
        return prontuarioService.vacinasDoAnimal(id, nome);
    }

    @GetMapping("/{id}/historico")
    public HistoricoDTO.Response historico(@PathVariable Long id) {
        return prontuarioService.historico(id);
    }
}
