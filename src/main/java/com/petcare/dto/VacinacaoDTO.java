package com.petcare.dto;

import com.petcare.model.Vacinacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class VacinacaoDTO {

    private VacinacaoDTO() {
    }

    public record Request(
            @NotNull(message = "Animal é obrigatório") Long animalId,
            @NotBlank(message = "Nome da vacina é obrigatório") @Size(max = 120) String nomeVacina,
            @NotNull(message = "Data de aplicação é obrigatória")
            @PastOrPresent(message = "Data de aplicação não pode ser futura") LocalDate dataAplicacao,
            @NotBlank(message = "Lote é obrigatório") @Size(max = 60) String lote) {
    }

    public record Response(Long id, Long animalId, String animalNome, String nomeVacina,
                           LocalDate dataAplicacao, String lote) {
        public static Response de(Vacinacao v) {
            return new Response(v.getId(), v.getAnimal().getId(), v.getAnimal().getNome(),
                    v.getNomeVacina(), v.getDataAplicacao(), v.getLote());
        }
    }
}
