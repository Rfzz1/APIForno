package com.rafael.monitor_forno.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.FornoSilenciarBuzzerDTO;
import com.rafael.monitor_forno.dto.TemporizadorWSDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.ServicoIndisponivelException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.websocket.FornoSessionRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;

@Service
public class FornoComandoWsService {

    private final FornoSessionRegistry fornoSessionRegistry;
    private final FornoRepository fornoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final TaskScheduler taskScheduler;

    public FornoComandoWsService(FornoSessionRegistry fornoSessionRegistry, FornoRepository fornoRepository, UsuarioRepository usuarioRepository, ObjectMapper objectMapper, TaskScheduler taskScheduler) {
        this.fornoSessionRegistry = fornoSessionRegistry;
        this.fornoRepository = fornoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
        this.taskScheduler = taskScheduler;
    }

    public void silenciarBuzzer(String serialNumber, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RecursoNaoEncontradoException("Usuario não encontrado")
        );

        Forno forno = fornoRepository.findBySerialNumber(serialNumber).orElseThrow(
                () -> new RecursoNaoEncontradoException("Forno não encontrado")
        );

        if (forno.getUsuario() == null || !forno.getUsuario().equals(usuario)) {
            throw new AcessoNegadoException(
                    "Este forno não pertence ao usuário logado."
            );
        }

        WebSocketSession session = fornoSessionRegistry.buscar(serialNumber);

        if (session == null || !session.isOpen()) {
            throw new RecursoNaoEncontradoException(
                    "Forno não está conectado no momento."
            );
        }

        FornoSilenciarBuzzerDTO dto = toFornoSilenciarBuzzerDTO(forno);

        try {
            String json = objectMapper.writeValueAsString(dto);
            session.sendMessage(new TextMessage(json));
            forno.setMuted(true);
            fornoRepository.save(forno);
        } catch (Exception e) {
            throw new ServicoIndisponivelException("Falha ao enviar comando para o forno", e);
        }
    }

    public void dispararBuzzer(String serialNumber, Temporizador temporizador) {
        dispararBuzzer(serialNumber, temporizador, 1);
    }

    private void dispararBuzzer(String serialNumber, Temporizador temporizador, int tentativas) {
        WebSocketSession session = fornoSessionRegistry.buscar(serialNumber);
        boolean enviado = false;

        if (session != null && session.isOpen()) {
            try {
                TemporizadorWSDTO dto = toWSDTO(temporizador);
                String json = objectMapper.writeValueAsString(dto);
                session.sendMessage(new TextMessage(json));
                enviado = true;
            } catch (IOException e) {
                // Falha pontual, prossegue para o reagendamento
            }
        }

        if (!enviado && tentativas < 3) {
            taskScheduler.schedule(
                    () -> dispararBuzzer(serialNumber, temporizador, tentativas + 1),
                    Instant.now().plusSeconds(1)
            );
        } else if (!enviado) {
            System.err.println("Falha ao enviar comando de disparo do buzzer para o forno " + serialNumber + " após 3 tentativas.");
        }
    }

    public FornoSilenciarBuzzerDTO toFornoSilenciarBuzzerDTO(Forno forno) {
        return FornoSilenciarBuzzerDTO.builder()
                .serialNumber(forno.getSerialNumber())
                .acao("MUTE")
                .muted(true)
                .build();
    }

    private TemporizadorWSDTO toWSDTO(Temporizador temporizador) {
        return TemporizadorWSDTO.builder()
                .duracaoSegundos(temporizador.getDuracaoSegundos())
                .acao("DISPARAR")
                .serialNumber(temporizador.getForno().getSerialNumber())
                .build();
    }
}