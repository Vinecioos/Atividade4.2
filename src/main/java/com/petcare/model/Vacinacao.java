package com.petcare.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "vacinacoes")
public class Vacinacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(name = "nome_vacina", nullable = false, length = 120)
    private String nomeVacina;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(nullable = false, length = 60)
    private String lote;

    protected Vacinacao() {
    }

    public Vacinacao(Animal animal, String nomeVacina, LocalDate dataAplicacao, String lote) {
        this.animal = animal;
        this.nomeVacina = nomeVacina;
        this.dataAplicacao = dataAplicacao;
        this.lote = lote;
    }

    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public String getNomeVacina() { return nomeVacina; }
    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public String getLote() { return lote; }
}
