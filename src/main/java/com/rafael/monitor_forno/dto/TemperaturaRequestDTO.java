package com.rafael.monitor_forno.dto;

import lombok.NoArgsConstructor;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemperaturaRequestDTO {

    private Double temperaturaAtual;
    private Double temperaturaUltima;
    private UUID sessaoId;
}
