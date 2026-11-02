package com.rafael.monitor_forno.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.EventoRepository;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.EventoResponseDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoEmFormatoInvalido;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.mappers.EventoMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.rafael.monitor_forno.enums.eventos.EventoSistema.*;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final SessaoRepository sessaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FornoRepository fornoRepository;
    private final NotificacaoService notificacaoService;

    @Value("${app.notificacao.icone.critico}")
    private String logoCriticoUrl;

    @Value("${app.notificacao.icone.alerta}")
    private String logoAlertaUrl;

    public EventoService(EventoRepository eventoRepository, SessaoRepository sessaoRepository, EventoMapper eventoMapper, UsuarioRepository usuarioRepository, FornoRepository fornoRepository, NotificacaoService notificacaoService) {
        this.eventoRepository = eventoRepository;
        this.sessaoRepository = sessaoRepository;
        this.eventoMapper = eventoMapper;
        this.usuarioRepository = usuarioRepository;
        this.fornoRepository = fornoRepository;
        this.notificacaoService = notificacaoService;
    }

    private Usuario buscarUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
    }

    @Transactional
    public void registrarEvento(EventoRequestDTO dto, UUID fornoId) throws FirebaseMessagingException {

        Forno forno = fornoRepository.findById(fornoId)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado: " + fornoId
                        )
                );

        if (dto.getTipo() == null) {
            throw new RecursoEmFormatoInvalido(
                    "Tipo do evento não informado"
            );
        }
        Sessao sessao = sessaoRepository
                .findByIdAndForno(dto.getSessaoId(), forno)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Sessão não pertence ao usuário logado"
                        )
                );

        Evento evento = new Evento();
        evento.setSessao(sessao);
        evento.setTipo(dto.getTipo());
        evento.setCriadoEm(Instant.now());
        eventoRepository.save(evento);

        if(dto.getTipo().equals(CRITICO_ENTRADA)) {
            // Enviar notificação para o usuário do forno
            notificacaoService.dispararNotificacaoInterna(forno, "Evento crítico detectado no forno: " + forno.getNome(), logoCriticoUrl);
        } else if (dto.getTipo().equals(ALERTA_ENTRADA)) {
            // Enviar notificação para o usuário do forno
            notificacaoService.dispararNotificacaoInterna(forno, "Evento de alerta detectado no forno: " + forno.getNome(), logoAlertaUrl);
        } else if (dto.getTipo().equals(ERRO_SENSOR_ENTRADA)) {
            return; // Não enviar notificação para outros tipos de eventos
        } else {

            if (forno.isMuted()) {
                forno.setMuted(false);
                fornoRepository.save(forno);
                return; // Não enviar notificação se o forno estiver silenciado
            }

            return;
        }

    }

    public void deleteById(UUID id, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Evento evento = eventoRepository.findByIdAndSessaoFornoUsuario(id, usuario)
                .orElseThrow(() ->
                        new AcessoNegadoException(
                                "Evento não pertence ao forno"
                        )
                );

        eventoRepository.delete(evento);
    }

    public List<EventoResponseDTO> findAllByFornoUsuario(String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        return eventoRepository.findAllBySessaoFornoUsuario(usuario)
                .stream()
                .map(eventoMapper::toEventoDTO)
                .toList();
    }

    public List<EventoResponseDTO> findAllByFornoAndUsuario(UUID fornoId, String email) {

        List<Evento> eventos = eventoRepository.findAllBySessaoFornoIdAndSessaoFornoUsuarioEmail(fornoId, email);

        if (!fornoRepository.existsById(fornoId)) {
            throw new RecursoNaoEncontradoException("Forno não encontrado: " + fornoId);
        }

        return eventos.stream()
                .map(eventoMapper::toEventoDTO)
                .toList();

    }

    public List<EventoResponseDTO> findLastThreeByFornoAndUsuario(UUID fornoId, String email) {

        if (!fornoRepository.existsById(fornoId)) {
            throw new RecursoNaoEncontradoException("Forno não encontrado: " + fornoId);
        }

        return eventoRepository.findTopThreeBySessaoFornoIdAndSessaoFornoUsuarioEmailOrderByCriadoEmDesc(fornoId, email)
                .stream()
                .map(eventoMapper::toEventoDTO)
                .toList();
    }


    public EventoResponseDTO findById(UUID id, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Evento evento = eventoRepository.findByIdAndSessaoFornoUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException("Evento não pertence ao forno")
                );

        return eventoMapper.toEventoDTO(evento);
    }
}
