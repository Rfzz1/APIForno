package com.rafael.monitor_forno.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FornoWebSocketHandler extends TextWebSocketHandler {

    // Mapeia: Key = serialNumber, Value = WebSocketSession
    private final Map<String, WebSocketSession> sessionsBySerial = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String serialNumber = extrairSerialNumber(session);

        if (serialNumber != null) {
            sessionsBySerial.put(serialNumber, session);
            System.out.println("ESP32 conectado! Serial: " + serialNumber);
        } else {
            // Rejeita a conexão se não enviar o serialNumber
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String serialNumber = extrairSerialNumber(session);
        if (serialNumber != null) {
            sessionsBySerial.remove(serialNumber);
            System.out.println("ESP32 desconectado. Serial: " + serialNumber);
        }
    }

    // Envia mensagem APENAS para o forno especificado
    public void enviarComandoParaForno(String serialNumber, String payload) throws IOException {
        WebSocketSession session = sessionsBySerial.get(serialNumber);

        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(payload));
        } else {
            // Opcional: Tratar caso o ESP32 esteja offline no WebSocket no momento
            System.out.println("AVISO: ESP32 " + serialNumber + " não está conectado ao WebSocket.");
        }
    }

    // Método utilitário para extrair ?serialNumber=... da URL de conexão
    private String extrairSerialNumber(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null && uri.getQuery() != null) {
            return UriComponentsBuilder.fromUri(uri)
                    .build()
                    .getQueryParams()
                    .getFirst("serialNumber");
        }
        return null;
    }
}