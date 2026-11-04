package com.petcare.controller;

import com.petcare.dto.ConsultaDTO;
import com.petcare.dto.VeterinarioDTO;
import com.petcare.model.Especie;
import com.petcare.service.ConsultaService;
import com.petcare.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService service;
    private final ConsultaService consultaService;

    public VeterinarioController(VeterinarioService service, ConsultaService consultaService) {
        this.service = service;
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<VeterinarioDTO.Response> criar(@Valid @RequestBody VeterinarioDTO.Request req) {
        VeterinarioDTO.Response criado = service.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    /** Filtro opcional: ?especie=CAO (apenas veterinários habilitados para a espécie). */
    @GetMapping
    public List<VeterinarioDTO.Response> listar(@RequestParam(required = false) Especie especie) {
        return service.listar(especie);
    }

    @GetMapping("/{id}")
    public VeterinarioDTO.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    /** Agenda do veterinário no período: ?inicio=2030-03-10T00:00:00&fim=2030-03-11T00:00:00 */
    @GetMapping("/{id}/agenda")
    public List<ConsultaDTO.Response> agenda(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return consultaService.agendaDoVeterinario(id, inicio, fim);
    }
}
