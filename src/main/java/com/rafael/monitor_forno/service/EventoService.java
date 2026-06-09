package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.repository.EventoRepository;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.dto.EventoDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.exception.RecursoEmFormatoInvalido;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.mappers.EventoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final SessaoRepository sessaoRepository;

    public EventoService(EventoRepository eventoRepository, SessaoRepository sessaoRepository, EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.sessaoRepository = sessaoRepository;
        this.eventoMapper = eventoMapper;
    }

    public void registrarEvento(EventoRequestDTO dto) {

        if (dto.getTipo() == null) {
            throw new RecursoEmFormatoInvalido(
                    "Tipo do evento não informado"
            );
        }

        Sessao sessao = sessaoRepository
                .findById(dto.getSessaoId())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Sessão não encontrada " + dto.getSessaoId()
                        )
                );

        Evento evento = new Evento();
        evento.setSessao(sessao);
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
                .map(eventoMapper::toEventoDTO)
                .toList();
    }

    public EventoDTO findById(UUID id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException("Evento não encontrado: " + id)
                );

        return eventoMapper.toEventoDTO(evento);
    }
}
