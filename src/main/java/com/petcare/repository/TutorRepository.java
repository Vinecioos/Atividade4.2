package com.petcare.repository;

import com.petcare.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByCpf(String cpf);

    Optional<Tutor> findByCpf(String cpf);

    /** Consulta personalizada 1: busca tutores pelo nome (parcial, sem diferenciar maiúsculas). */
    @Query("select t from Tutor t where lower(t.nome) like lower(concat('%', :nome, '%')) order by t.nome")
    List<Tutor> buscarPorNome(@Param("nome") String nome);
}
