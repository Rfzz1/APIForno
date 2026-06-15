package com.rafael.monitor_forno.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstatisticasDTO {

    private Long tempoTotalLigado;
    private Integer quantidadeSessoes;
    private Double temperaturaMaxima;
    private Long alertas;
    private Long criticos;
    private Long errosSensor;

}
