package com.petcare.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "animais")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Especie especie;

    @Column(nullable = false, length = 80)
    private String raca;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    // Vários animais pertencem a um tutor
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    protected Animal() {
    }

    public Animal(String nome, Especie especie, String raca, LocalDate dataNascimento, Tutor tutor) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.tutor = tutor;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Especie getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Tutor getTutor() { return tutor; }
}
