package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorRequestDTO {

    @NotBlank(message = "horarioFim é obrigatório")
    private ZonedDateTime horarioFim;

    @NotBlank(message = "horarioInicio é obrigatório")
    private ZonedDateTime horarioInicio;
}
