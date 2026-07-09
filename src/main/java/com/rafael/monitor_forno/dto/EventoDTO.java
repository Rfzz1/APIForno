package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoDTO {

    private UUID id;
    private LocalDateTime criadoEm;
    private EventoSistema tipo;
    private UUID fornoId;
    private UUID sessaoId;

}
