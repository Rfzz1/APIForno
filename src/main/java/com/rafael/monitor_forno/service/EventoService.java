package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.EventoRepository;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.EventoDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
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
    private final UsuarioRepository usuarioRepository;

    public EventoService(EventoRepository eventoRepository, SessaoRepository sessaoRepository, EventoMapper eventoMapper, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.sessaoRepository = sessaoRepository;
        this.eventoMapper = eventoMapper;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarEvento(EventoRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        if (dto.getTipo() == null) {
            throw new RecursoEmFormatoInvalido(
                    "Tipo do evento não informado"
            );
        }

        Sessao sessao = sessaoRepository
                .findByIdAndUsuario(dto.getSessaoId(), usuario)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Sessão não pertence ao usuário logado"
                        )
                );

        Evento evento = new Evento();
        evento.setSessao(sessao);
        evento.setTipo(dto.getTipo());
        evento.setUsuario(usuario);
        evento.setCriadoEm(LocalDateTime.now());
        eventoRepository.save(evento);
    }

    public void deleteById(UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Evento evento = eventoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() ->
                        new AcessoNegadoException(
                                "Evento não pertence ao usuário logado"
                        ));

        eventoRepository.delete(evento);
    }

    public List<EventoDTO> findAllByUsuario(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        return eventoRepository.findAllByUsuario(usuario)
                .stream()
                .map(eventoMapper::toEventoDTO)
                .toList();
    }

    public EventoDTO findById(UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Evento evento = eventoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException("Evento não pertence ao usuario logado")
                );

        return eventoMapper.toEventoDTO(evento);
    }
}
