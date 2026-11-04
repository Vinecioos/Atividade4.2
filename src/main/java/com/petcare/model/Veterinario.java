package com.petcare.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "veterinarios")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String crmv;

    // Espécies que o veterinário está habilitado a atender
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "veterinario_especies", joinColumns = @JoinColumn(name = "veterinario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false, length = 20)
    private Set<Especie> especies = new HashSet<>();

    protected Veterinario() {
    }

    public Veterinario(String nome, String crmv, Set<Especie> especies) {
        this.nome = nome;
        this.crmv = crmv;
        this.especies = new HashSet<>(especies);
    }

    /** Regra de negócio: o veterinário só atende espécies para as quais está habilitado. */
    public boolean atende(Especie especie) {
        return especies.contains(especie);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCrmv() { return crmv; }
    public Set<Especie> getEspecies() { return especies; }
}
