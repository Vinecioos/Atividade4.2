package com.petcare.dto;

import java.util.List;

public final class HistoricoDTO {

    private HistoricoDTO() {
    }

    /** Histórico completo de um animal: dados, atendimentos e vacinas. */
    public record Response(AnimalDTO.Response animal,
                           List<AtendimentoDTO.Response> atendimentos,
                           List<VacinacaoDTO.Response> vacinas) {
    }
}
