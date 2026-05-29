package com.rafael.monitor_forno.database.model;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sessao")
public class Sessao {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime inicioSessao;
    private LocalDateTime fimSessao;
    private Long duracaoSegundos;

    @Enumerated(EnumType.STRING)
    private EstadoForno estadoFornoFinal;
}
