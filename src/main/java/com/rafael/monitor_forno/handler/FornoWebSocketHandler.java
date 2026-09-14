package com.rafael.monitor_forno.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FornoWebSocketHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> sessoesAtivas = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession sessao, TextMessage mensagem) throws Exception {

        String textoRecebido = mensagem.getPayload();
        System.out.println("Mensagem recebida: " + textoRecebido);

        sessao.sendMessage(new TextMessage(textoRecebido));

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) throws Exception {

        String rota = sessao.getUri().getPath();
        String[] partesRota = rota.split("/");
        sessoesAtivas.put(partesRota[2], sessao);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession sessao, CloseStatus status) throws Exception {

        String rota = sessao.getUri().getPath();
        String[] partesRota = rota.split("/");
        sessoesAtivas.remove(partesRota[2]);

    }

}