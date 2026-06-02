package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.repository.EventoRepository;
import com.rafael.monitor_forno.dto.EventoDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.exception.EstadoInvalidoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import jdk.jfr.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public void deleteById(UUID id) {

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Evento não encontrado"
                        ));

        eventoRepository.delete(evento);
    }

    public List<EventoDTO> findAll() {
        return eventoRepository.findAll()
                .stream()
                .map(this::toEventoDTO)
                .toList();
    }

    public EventoDTO findById(UUID id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException("Evento não encontrado: " + id)
                );

        return toEventoDTO(evento);
    }

    private EventoDTO toEventoDTO(Evento evento) {

        return EventoDTO.builder()
                .id(evento.getId())
                .criadoEm(evento.getCriadoEm())
                .tipo(evento.getTipo())
                .build();
    }
}
