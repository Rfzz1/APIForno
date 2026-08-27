package com.rafael.monitor_forno.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String logoBase64;

    @Column(nullable = false)
    private String mensagem;

    @Column(nullable = false)
    private String nomeApp;

    @Column(nullable = false)
    private LocalTime horaEnvio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forno_id")
    private Forno forno;
}
