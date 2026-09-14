package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
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

    public FornoComandoWsService(FornoSessionRegistry fornoSessionRegistry, FornoRepository fornoRepository, UsuarioRepository usuarioRepository) {
        this.fornoSessionRegistry = fornoSessionRegistry;
        this.fornoRepository = fornoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void silenciarBuzzer(String serialNumber, String email) {

        Usuario usuario =  usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        Forno forno = fornoRepository.findBySerialNumber(serialNumber).orElseThrow(
                () -> new RuntimeException("Forno no encontrado")
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

        try {
            session.sendMessage(new TextMessage("SILENCIAR_BUZZER"));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar comando para o forno", e);
        }
    }

}
