package com.rafael.monitor_forno.dto;

import lombok.NoArgsConstructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemperaturaRequestDTO {

    private Double temperaturaAtual;
    private Double temperaturaUltima;
}
