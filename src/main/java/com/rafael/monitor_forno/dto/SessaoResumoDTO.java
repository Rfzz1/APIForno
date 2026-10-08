package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import com.rafael.monitor_forno.enums.estados.EstadoSistema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SessaoResumoDTO {

    private UUID id;
    private Instant inicioSessao;
    private Instant fimSessao;
    private EstadoForno estadoFornoFinal;
    private EstadoSistema estadoSistemaFinal;
}
