package com.rafael.monitor_forno.database.model;


import com.rafael.monitor_forno.enums.estados.EstadoForno;
import com.rafael.monitor_forno.enums.estados.EstadoSistema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="telemetrias")
public class Telemetria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double temperaturaAtual;

    @Column(nullable = false)
    private Double temperaturaUltima;

    @Enumerated(EnumType.STRING)
    private EstadoForno estadoForno;

    @Enumerated(EnumType.STRING)
    private EstadoSistema estadoSistema;

    private Long tempoLigadoMinutos;

    private LocalDateTime criadoEm;
}
