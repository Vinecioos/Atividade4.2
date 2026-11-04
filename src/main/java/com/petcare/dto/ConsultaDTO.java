package com.petcare.dto;

import com.petcare.model.Consulta;
import com.petcare.model.Especie;
import com.petcare.model.StatusConsulta;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class ConsultaDTO {

    private ConsultaDTO() {
    }

    public record Request(
            @NotNull(message = "Animal é obrigatório") Long animalId,
            @NotNull(message = "Veterinário é obrigatório") Long veterinarioId,
            @NotBlank(message = "Local é obrigatório") @Size(max = 150) String local,
            @NotNull(message = "Data e horário são obrigatórios")
            @Future(message = "O agendamento deve ser em data/horário futuro") LocalDateTime dataHora) {
    }

    public record Response(Long id, Long animalId, String animalNome, Especie especie,
                           Long veterinarioId, String veterinarioNome, String local,
                           LocalDateTime dataHora, StatusConsulta status) {
        public static Response de(Consulta c) {
            return new Response(c.getId(),
                    c.getAnimal().getId(), c.getAnimal().getNome(), c.getAnimal().getEspecie(),
                    c.getVeterinario().getId(), c.getVeterinario().getNome(),
                    c.getLocal(), c.getDataHora(), c.getStatus());
        }
    }
}
