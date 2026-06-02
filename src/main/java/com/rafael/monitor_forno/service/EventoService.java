package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.repository.EventoRepository;
import com.rafael.monitor_forno.dto.EventoDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.exception.EstadoInvalidoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public void registrarEvento(EventoRequestDTO dto) {

        if (dto.getTipo() == null) {
            throw new EstadoInvalidoException(
                    "Tipo do evento não informado"
            );
        }

        Evento evento = new Evento();
        evento.setTipo(dto.getTipo());
        evento.setCriadoEm(LocalDateTime.now());
        eventoRepository.save(evento);
    }

    public List<EventoDTO> findAll() {
        return eventoRepository.findAll()
                .stream()
                .map(this::toEventoDTO)
                .toList();
    }

    private EventoDTO toEventoDTO(Evento evento) {

        return EventoDTO.builder()
                .id(evento.getId())
                .criadoEm(evento.getCriadoEm())
                .tipo(evento.getTipo())
                .build();
    }
}
