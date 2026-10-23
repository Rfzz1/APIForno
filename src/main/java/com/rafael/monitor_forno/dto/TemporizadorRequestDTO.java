package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorRequestDTO {

    private ZonedDateTime horarioFim;
    private ZonedDateTime horarioInicio;
}
