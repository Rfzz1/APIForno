package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // Pega a query string bruta da URL, ex: "token=eyJhbGc..."
        String query = request.getURI().getQuery();

        if (query == null || !query.startsWith("token=")) {
            return false; // rejeita o handshake — nem vira WebSocket
        }

        String token = query.substring("token=".length());

        try {
            Claims claims = jwtService.extrairTodasClaims(token);
            String tipo = claims.get("tipo", String.class);

            if (!"FORNO".equals(tipo)) {
                return false; // só fornos podem conectar nesse endpoint
            }

            String serialNumber = claims.getSubject();

            // Aqui é a parte-chave: guardamos o serialNumber nos "attributes".
            // Esse Map vira acessível depois via session.getAttributes()
            // em QUALQUER momento da vida dessa conexão.
            // Assim evita que outros fornos acessem o serialNumber dessa sessão
            attributes.put("serialNumber", serialNumber);

            return true; // autoriza o handshake a continuar

        } catch (Exception e) {
            return false; // token inválido ou expirado
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // não precisamos fazer nada aqui por enquanto
    }
}