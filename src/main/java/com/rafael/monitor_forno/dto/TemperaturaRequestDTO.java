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
    private Double temperaturaExterna;
    private UUID sessaoId;
    private UUID fornoId;
}
