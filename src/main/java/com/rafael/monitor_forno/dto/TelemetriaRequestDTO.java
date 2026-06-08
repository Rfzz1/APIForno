package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelemetriaRequestDTO {

    private Double temperaturaAtual;

    private Double temperaturaUltima;

    private EstadoForno estadoForno;

    private String estadoSistema;

    private Long tempoLigadoMinutos;

}
