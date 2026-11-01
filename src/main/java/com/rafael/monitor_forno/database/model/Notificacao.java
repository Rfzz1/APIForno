package com.rafael.monitor_forno.database.model;

import com.rafael.monitor_forno.enums.notificacao.StatusN;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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
    private String logoUrl;

    @Column(nullable = false)
    private String mensagem;

    @Column(nullable = false)
    private String nomeApp;

    @Column(nullable = false)
    private Instant horaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusN statusN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forno_id")
    private Forno forno;
}
