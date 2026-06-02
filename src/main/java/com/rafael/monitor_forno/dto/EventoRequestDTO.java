package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import lombok.NoArgsConstructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoRequestDTO {

    private EventoSistema tipo;
}
