package com.petcare.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Consulta agendada.
 *
 * Para impedir dois agendamentos do mesmo veterinário no mesmo horário (inclusive em
 * requisições concorrentes), usamos uma "chave de agenda" (veterinário + data/hora) com
 * restrição UNIQUE no banco. A chave só existe enquanto a consulta ocupa o horário;
 * ao cancelar, ela vira NULL e o horário é liberado (o banco aceita vários NULL em UNIQUE).
 */
@Entity
@Table(name = "consultas",
        uniqueConstraints = @UniqueConstraint(name = "uk_consulta_agenda", columnNames = "chave_agenda"))
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Column(name = "local_consulta", nullable = false, length = 150)
    private String local;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConsulta status;

    @Column(name = "chave_agenda", length = 60)
    private String chaveAgenda;

    protected Consulta() {
    }

    public Consulta(Animal animal, Veterinario veterinario, String local, LocalDateTime dataHora) {
        this.animal = animal;
        this.veterinario = veterinario;
        this.local = local;
        this.dataHora = dataHora;
        this.status = StatusConsulta.AGENDADA;
        this.chaveAgenda = gerarChave(veterinario.getId(), dataHora);
    }

    public static String gerarChave(Long veterinarioId, LocalDateTime dataHora) {
        return veterinarioId + "|" + dataHora;
    }

    public void cancelar() {
        this.status = StatusConsulta.CANCELADA;
        this.chaveAgenda = null; // libera o horário do veterinário
    }

    public void marcarRealizada() {
        this.status = StatusConsulta.REALIZADA;
    }

    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public Veterinario getVeterinario() { return veterinario; }
    public String getLocal() { return local; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusConsulta getStatus() { return status; }
}
