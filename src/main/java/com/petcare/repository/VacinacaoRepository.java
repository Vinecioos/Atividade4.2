package com.petcare.repository;

import com.petcare.model.Vacinacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacinacaoRepository extends JpaRepository<Vacinacao, Long> {

    List<Vacinacao> findByAnimalIdOrderByDataAplicacaoDesc(Long animalId);

    /** Consulta personalizada 5: vacinas de um animal filtradas pelo nome (parcial). */
    @Query("""
            select v from Vacinacao v
            where v.animal.id = :animalId
              and lower(v.nomeVacina) like lower(concat('%', :nome, '%'))
            order by v.dataAplicacao desc
            """)
    List<Vacinacao> buscarPorNomeDaVacina(@Param("animalId") Long animalId, @Param("nome") String nome);
}
