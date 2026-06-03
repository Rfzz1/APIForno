package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorRequestDTO {

    private LocalDateTime horarioFim;
}
