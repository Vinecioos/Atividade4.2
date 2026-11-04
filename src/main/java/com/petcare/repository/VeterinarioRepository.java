package com.petcare.repository;

import com.petcare.model.Especie;
import com.petcare.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    boolean existsByCrmv(String crmv);

    /** Consulta personalizada 3: veterinários habilitados a atender determinada espécie. */
    @Query("select distinct v from Veterinario v join v.especies e where e = :especie order by v.nome")
    List<Veterinario> buscarHabilitadosParaEspecie(@Param("especie") Especie especie);
}
