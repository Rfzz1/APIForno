package com.rafael.monitor_forno.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class FornoSessionRegistry {

    // ConcurrentHashMap porque múltiplas threads (conexões diferentes,
    // requests REST simultâneos) vão ler/escrever aqui ao mesmo tempo.
    // Um HashMap comum não é seguro para isso e pode corromper dados.
    private final Map<String, WebSocketSession> sessoesAtivas = new ConcurrentHashMap<>();

    public void registrar(String serialNumber, WebSocketSession session) {
        sessoesAtivas.put(serialNumber, session);
    }

    public void remover(String serialNumber) {
        sessoesAtivas.remove(serialNumber);
    }

    public WebSocketSession buscar(String serialNumber) {
        return sessoesAtivas.get(serialNumber);
    }

    public boolean estaConectado(String serialNumber) {
        WebSocketSession session = sessoesAtivas.get(serialNumber);
        return session != null && session.isOpen();
    }
}