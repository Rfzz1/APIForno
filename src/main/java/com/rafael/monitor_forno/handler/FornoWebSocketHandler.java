package com.rafael.monitor_forno.handler;

import com.rafael.monitor_forno.websocket.FornoSessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FornoWebSocketHandler extends TextWebSocketHandler {

    private final FornoSessionRegistry fornoSessionRegistry;

    public FornoWebSocketHandler(FornoSessionRegistry fornoSessionRegistry) {
        this.fornoSessionRegistry = fornoSessionRegistry;
    }

    @Override
    protected void handleTextMessage(WebSocketSession sessao, TextMessage mensagem) throws Exception {

        String textoRecebido = mensagem.getPayload();
        System.out.println("Mensagem recebida: " + textoRecebido);

        sessao.sendMessage(new TextMessage(textoRecebido));

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) throws Exception {

        String serialNumber = (String) sessao.getAttributes().get("serialNumber");
        fornoSessionRegistry.registrar(serialNumber, sessao);
        sessao.sendMessage(new TextMessage("Conexão estabelecida com sucesso. Forno: " + serialNumber));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession sessao, CloseStatus status) throws Exception {

        String serialNumber =  (String) sessao.getAttributes().get("serialNumber");
        fornoSessionRegistry.remover(serialNumber);
        System.out.println("Forno desconectado: " + serialNumber + " - " + status);

    }

}