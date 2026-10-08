package com.rafael.monitor_forno.dto;

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
public class TemperaturaDTO {

    private UUID id;
    private Double temperaturaAtual;
    private Double temperaturaUltima;
    private Instant registradoEm;
}
