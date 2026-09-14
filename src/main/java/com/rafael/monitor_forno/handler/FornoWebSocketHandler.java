package com.rafael.monitor_forno.handler;

import com.rafael.monitor_forno.exception.FornoDesconectadoException;
import com.rafael.monitor_forno.websocket.FornoSessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FornoWebSocketHandler extends TextWebSocketHandler {

    private final FornoSessionRegistry fornoSessionRegistry;

    public FornoWebSocketHandler(FornoSessionRegistry fornoSessionRegistry) {
        this.fornoSessionRegistry = fornoSessionRegistry;
    }

    // Mapeia: Key = serialNumber, Value = WebSocketSession
    //Permite a conexão
    private final Map<String, WebSocketSession> sessionsBySerial = new ConcurrentHashMap<>();

    //Quando esp32 conecta
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String serialNumber = (String) session.getAttributes().get("serialNumber");

        fornoSessionRegistry.registrar(serialNumber,session);

        System.out.println("Forno conectado: " + serialNumber + " (sessão: " + session.getId() + ")");
        session.sendMessage(new TextMessage("Autenticado como forno: " + serialNumber));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String serialNumber = (String) session.getAttributes().get("serialNumber");

        fornoSessionRegistry.remover(serialNumber);

        System.out.println("Forno desconectado: " + serialNumber + " - " + status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Chamado TODA VEZ que chega uma mensagem de texto nessa conexão.
        String payload = message.getPayload();
        System.out.println("Mensagem recebida de " + session.getId() + ": " + payload);

        // Por enquanto, só devolve um eco pra confirmar que recebeu.
        session.sendMessage(new TextMessage("Servidor recebeu: " + payload));
    }
}