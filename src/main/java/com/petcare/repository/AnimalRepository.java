package com.petcare.repository;

import com.petcare.model.Animal;
import com.petcare.model.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByTutorIdOrderByNome(Long tutorId);

    List<Animal> findAllByOrderByNome();

    /** Consulta personalizada 2: animais de uma espécie, já carregando o tutor (evita N+1). */
    @Query("select a from Animal a join fetch a.tutor where a.especie = :especie order by a.nome")
    List<Animal> buscarPorEspecieComTutor(@Param("especie") Especie especie);
}
