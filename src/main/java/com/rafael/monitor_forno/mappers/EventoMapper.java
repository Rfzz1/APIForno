package com.rafael.monitor_forno.mappers;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.dto.EventoDTO;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public EventoDTO toEventoDTO(Evento evento) {

        return EventoDTO.builder()
                .id(evento.getId())
                .criadoEm(evento.getCriadoEm())
                .tipo(evento.getTipo())
                .build();
    }
}
