package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import com.rafael.monitor_forno.enums.estados.EstadoSistema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SessaoDetalhesDTO {

    private UUID id;
    private LocalDateTime inicioSessao;
    private LocalDateTime fimSessao;
    private EstadoForno estadoFornoFinal;
    private EstadoSistema estadoSistema;
    private long duracaoSegundos;

}
