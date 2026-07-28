package com.rafael.monitor_forno.handler;

import com.rafael.monitor_forno.exception.FornoDesconectadoException;
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
    //Permite a conexão
    private final Map<String, WebSocketSession> sessionsBySerial = new ConcurrentHashMap<>();

    //Quando esp32 conecta
    @Override
    public void afterConnectionEstablished(WebSocketSession session){

        try {
            //Extrai o query param da url
            String serialNumber = extrairSerialNumber(session);

            if (serialNumber != null) {
                sessionsBySerial.put(serialNumber, session);
                System.out.println("ESP32 conectado! Serial: " + serialNumber);
            } else {
                // Rejeita a conexão se não enviar o serialNumber
                session.close(CloseStatus.BAD_DATA.withReason("Serial Number obrigatório"));
            }
        } catch (Exception e) {
                System.err.println("Erro ao conectar ESP32: " + e.getMessage());
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException ignored) {}
            }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String serialNumber = extrairSerialNumber(session);
        if (serialNumber != null) {
            //Limpa a memória
            sessionsBySerial.remove(serialNumber);
            System.out.println("ESP32 desconectado. Serial: " + serialNumber);
        }
    }

    // Envia mensagem APENAS para o forno especificado
    public void enviarComandoParaForno(String serialNumber, String payload) throws IOException {

        //Busca no Map se existe uma linha ABERTA para esse Serial
        WebSocketSession session = sessionsBySerial.get(serialNumber);

        if (session == null || !session.isOpen()) {
            // Se a sessão não existe, joga a exceção personalizada!
            throw new FornoDesconectadoException("Não foi possível mutar: o forno " + serialNumber + " está offline no momento.");
        }

        session.sendMessage(new TextMessage(payload));
    }

    private String extrairSerialNumber(WebSocketSession session) {
        //Pega a URL crua que o esp32 chamou
        URI uri = session.getUri();

        //Garanque que a URL exista e que possua parâmetros
        if (uri != null && uri.getQuery() != null) {
            //Particiona a URL
            return UriComponentsBuilder.fromUri(uri)
                    .build()
                    .getQueryParams()
                    .getFirst("serialNumber");
        }
        return null;
    }
}