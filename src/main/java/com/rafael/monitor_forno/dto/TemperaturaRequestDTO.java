package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Sessão é obrigatória")
    private UUID sessaoId;

    @NotNull(message = "Forno é obrigatório")
    private UUID fornoId;
}
