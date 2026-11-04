package com.petcare.dto;

import com.petcare.model.Especie;
import com.petcare.model.Veterinario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.TreeSet;

public final class VeterinarioDTO {

    private VeterinarioDTO() {
    }

    public record Request(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 120) String nome,
            @NotBlank(message = "CRMV é obrigatório") @Size(max = 20) String crmv,
            @NotEmpty(message = "Informe ao menos uma espécie atendida") Set<Especie> especies) {
    }

    public record Response(Long id, String nome, String crmv, Set<Especie> especies) {
        public static Response de(Veterinario v) {
            return new Response(v.getId(), v.getNome(), v.getCrmv(), new TreeSet<>(v.getEspecies()));
        }
    }
}
