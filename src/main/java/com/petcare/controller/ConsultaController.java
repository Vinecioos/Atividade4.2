package com.petcare.controller;

import com.petcare.dto.ConsultaDTO;
import com.petcare.model.StatusConsulta;
import com.petcare.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConsultaDTO.Response> agendar(@Valid @RequestBody ConsultaDTO.Request req) {
        ConsultaDTO.Response criada = service.agendar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    /** Filtros opcionais: ?animalId=1&veterinarioId=2&status=AGENDADA */
    @GetMapping
    public List<ConsultaDTO.Response> listar(@RequestParam(required = false) Long animalId,
                                             @RequestParam(required = false) Long veterinarioId,
                                             @RequestParam(required = false) StatusConsulta status) {
        return service.listar(animalId, veterinarioId, status);
    }

    @GetMapping("/{id}")
    public ConsultaDTO.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    /** Cancela o agendamento (o registro é mantido com status CANCELADA e o horário é liberado). */
    @PatchMapping("/{id}/cancelar")
    public ConsultaDTO.Response cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
