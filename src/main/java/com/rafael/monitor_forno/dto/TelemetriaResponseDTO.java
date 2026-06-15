package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import com.rafael.monitor_forno.enums.estados.EstadoSistema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelemetriaResponseDTO {

    private Double temperaturaAtual;

    private Double temperaturaUltima;

    private EstadoForno estadoForno;

    private EstadoSistema estadoSistema;

    private Long tempoLigadoMinutos;

    private LocalDateTime atualizadoEm;


}
