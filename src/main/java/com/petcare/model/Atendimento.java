package com.petcare.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Registro do prontuário: o que aconteceu em uma consulta. */
@Entity
@Table(name = "atendimentos")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cada consulta gera no máximo um atendimento
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false, unique = true)
    private Consulta consulta;

    @Column(nullable = false, length = 2000)
    private String diagnostico;

    @Column(length = 2000)
    private String observacoes;

    @Column(length = 2000)
    private String orientacoes;

    @Column(name = "registrado_em", nullable = false)
    private LocalDateTime registradoEm;

    protected Atendimento() {
    }

    public Atendimento(Consulta consulta, String diagnostico, String observacoes, String orientacoes) {
        this.consulta = consulta;
        this.diagnostico = diagnostico;
        this.observacoes = observacoes;
        this.orientacoes = orientacoes;
        this.registradoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Consulta getConsulta() { return consulta; }
    public String getDiagnostico() { return diagnostico; }
    public String getObservacoes() { return observacoes; }
    public String getOrientacoes() { return orientacoes; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
}
