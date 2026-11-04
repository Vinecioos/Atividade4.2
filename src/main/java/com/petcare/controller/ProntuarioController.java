package com.petcare.controller;

import com.petcare.dto.AtendimentoDTO;
import com.petcare.dto.VacinacaoDTO;
import com.petcare.service.ProntuarioService;
import com.petcare.util.AtendimentoDocumento;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class ProntuarioController {

    private final ProntuarioService service;

    public ProntuarioController(ProntuarioService service) {
        this.service = service;
    }

    // ----- Atendimentos -----

    @PostMapping("/atendimentos")
    public ResponseEntity<AtendimentoDTO.Response> registrarAtendimento(
            @Valid @RequestBody AtendimentoDTO.Request req) {
        AtendimentoDTO.Response criado = service.registrarAtendimento(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/atendimentos/{id}")
    public AtendimentoDTO.Response buscarAtendimento(@PathVariable Long id) {
        return service.buscarAtendimento(id);
    }

    /** Versão HTML do atendimento, pronta para impressão (abra no navegador e use Ctrl+P). */
    @GetMapping(value = "/atendimentos/{id}/imprimir", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> imprimir(@PathVariable Long id) {
        String html = AtendimentoDocumento.html(service.buscarAtendimento(id));
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8))
                .body(html);
    }

    // ----- Vacinação -----

    @PostMapping("/vacinacoes")
    public ResponseEntity<VacinacaoDTO.Response> registrarVacina(@Valid @RequestBody VacinacaoDTO.Request req) {
        VacinacaoDTO.Response criada = service.registrarVacina(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }
}
