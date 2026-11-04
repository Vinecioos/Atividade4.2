package com.petcare.dto;

import com.petcare.model.Animal;
import com.petcare.model.Especie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class AnimalDTO {

    private AnimalDTO() {
    }

    public record Request(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 80) String nome,
            @NotNull(message = "Espécie é obrigatória") Especie especie,
            @NotBlank(message = "Raça é obrigatória") @Size(max = 80) String raca,
            @NotNull(message = "Data de nascimento é obrigatória")
            @PastOrPresent(message = "Data de nascimento não pode ser futura") LocalDate dataNascimento,
            @NotNull(message = "Tutor responsável é obrigatório") Long tutorId) {
    }

    public record Response(Long id, String nome, Especie especie, String raca, LocalDate dataNascimento,
                           Long tutorId, String tutorNome) {
        public static Response de(Animal a) {
            return new Response(a.getId(), a.getNome(), a.getEspecie(), a.getRaca(), a.getDataNascimento(),
                    a.getTutor().getId(), a.getTutor().getNome());
        }
    }
}
