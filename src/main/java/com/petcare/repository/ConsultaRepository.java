package com.petcare.repository;

import com.petcare.model.Consulta;
import com.petcare.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    /** Verdadeiro se o veterinário já tem consulta ativa naquele horário. */
    boolean existsByChaveAgenda(String chaveAgenda);

    List<Consulta> findAllByOrderByDataHora();

    List<Consulta> findByAnimalIdOrderByDataHora(Long animalId);

    List<Consulta> findByVeterinarioIdOrderByDataHora(Long veterinarioId);

    /** Consulta personalizada 4: agenda de um veterinário em um período, por status. */
    @Query("""
            select c from Consulta c
            join fetch c.animal
            join fetch c.veterinario
            where c.veterinario.id = :veterinarioId
              and c.status = :status
              and c.dataHora >= :inicio
              and c.dataHora < :fim
            order by c.dataHora
            """)
    List<Consulta> buscarAgenda(@Param("veterinarioId") Long veterinarioId,
                                @Param("status") StatusConsulta status,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fim") LocalDateTime fim);
}
