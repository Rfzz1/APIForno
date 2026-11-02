package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
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

        System.out.println(">>> Tentativa de Handshake WS recebida da URL: " + request.getURI());

        List<String> tokens = request.getHeaders().get("Authorization");

        if (tokens == null || tokens.isEmpty()) {
            return false;
        }

        if (!tokens.get(0).startsWith("Bearer ")) {
            return false;
        }

        String token = tokens.get(0).replace("Bearer ", ""); // Remove "Bearer " do início

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
            System.out.println(">>> Erro no Handshake JWT: " + e.getMessage());
            e.printStackTrace();
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