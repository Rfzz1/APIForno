package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorRequestDTO {

    @NotNull(message = "horarioFim é obrigatório")
    private ZonedDateTime horarioFim;

    @NotNull(message = "horarioInicio é obrigatório")
    private ZonedDateTime horarioInicio;
}
