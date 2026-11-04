package com.petcare.repository;

import com.petcare.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    boolean existsByConsultaId(Long consultaId);

    List<Atendimento> findByConsultaAnimalIdOrderByRegistradoEmDesc(Long animalId);
}
