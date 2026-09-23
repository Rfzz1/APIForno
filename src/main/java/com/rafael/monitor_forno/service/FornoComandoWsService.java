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
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.websocket.FornoSessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class FornoComandoWsService {

    private final FornoSessionRegistry fornoSessionRegistry;
    private final FornoRepository fornoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public FornoComandoWsService(FornoSessionRegistry fornoSessionRegistry, FornoRepository fornoRepository, UsuarioRepository usuarioRepository, ObjectMapper objectMapper) {
        this.fornoSessionRegistry = fornoSessionRegistry;
        this.fornoRepository = fornoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    public void silenciarBuzzer(String serialNumber, String email) {

        Usuario usuario =  usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuario não encontrado")
        );

        Forno forno = fornoRepository.findBySerialNumber(serialNumber).orElseThrow(
                () -> new RuntimeException("Forno não encontrado")
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

        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar comando para o forno", e);
        }
    }

    public void dispararBuzzer(String serialNumber, Temporizador temporizador) {

        WebSocketSession session = fornoSessionRegistry.buscar(serialNumber);
        if (session == null || !session.isOpen()) {
            throw new RecursoNaoEncontradoException(
                    "Forno não está conectado no momento."
            );
        }

        TemporizadorWSDTO dto = toWSDTO(temporizador);

        try {

            String json = objectMapper.writeValueAsString(dto);
            session.sendMessage(new TextMessage(json));

        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar comando para o forno", e);
        }
    }

    public FornoSilenciarBuzzerDTO toFornoSilenciarBuzzerDTO(Forno forno) {
        return FornoSilenciarBuzzerDTO.builder()
                .serialNumber(forno.getSerialNumber())
                .acao("MUTE")
                .isMuted(true)
                .build();
    }

    private TemporizadorWSDTO toWSDTO(Temporizador temporizador) {
        return TemporizadorWSDTO.builder()
                .duracaoSegundos(temporizador.getDuracaoSegundos())
                .acao("DISPARAR")
                .build();
    }

}
