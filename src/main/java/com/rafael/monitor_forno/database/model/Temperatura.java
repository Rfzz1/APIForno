package com.rafael.monitor_forno.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="temperatura")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Temperatura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double temperaturaAtual;

    @Column (nullable = false)
    private Double temperaturaUltima;

    @Column(nullable = false)
    private LocalDateTime registradoEm;

    @ManyToOne
    @JoinColumn(name = "sessao_id")
    private Sessao sessao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="forno_id")
    private Forno forno;

}
