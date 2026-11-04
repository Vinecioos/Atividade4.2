package com.petcare.dto;

import com.petcare.model.Tutor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class TutorDTO {

    private TutorDTO() {
    }

    public record Request(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 120) String nome,
            @NotBlank(message = "CPF é obrigatório") String cpf,
            @NotBlank(message = "Telefone é obrigatório") @Size(max = 20) String telefone,
            @NotBlank(message = "Endereço é obrigatório") @Size(max = 255) String endereco) {
    }

    public record Response(Long id, String nome, String cpf, String telefone, String endereco) {
        public static Response de(Tutor t) {
            return new Response(t.getId(), t.getNome(), t.getCpf(), t.getTelefone(), t.getEndereco());
        }
    }
}
