package com.rafael.monitor_forno.database.model;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import com.rafael.monitor_forno.enums.estados.EstadoSistema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    private EstadoSistema estadoSistema;

    @OneToMany(mappedBy = "sessao")
    private List<Evento> eventos;

    @OneToMany(mappedBy = "sessao")
    private List<Temperatura> temperaturas;

    @ManyToOne
    @JoinColumn(name="usuario_id")
    private Usuario usuario;
}
