package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoResponseDTO {

    private UUID id;
    private Instant criadoEm;
    private EventoSistema tipo;
    private UUID fornoId;
    private UUID sessaoId;

}
