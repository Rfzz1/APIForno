package com.rafael.monitor_forno.mappers;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.dto.EventoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public EventoResponseDTO toEventoDTO(Evento evento) {

        return EventoResponseDTO.builder()
                .id(evento.getId())
                .criadoEm(evento.getCriadoEm())
                .tipo(evento.getTipo())
                .build();
    }
}
