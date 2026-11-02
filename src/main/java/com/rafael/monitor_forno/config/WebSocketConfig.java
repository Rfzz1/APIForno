package com.rafael.monitor_forno.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael.monitor_forno.handler.FornoWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket //Ativa suporte de websocket para spring boot
public class WebSocketConfig implements WebSocketConfigurer {

    private final FornoWebSocketHandler fornoWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final ObjectMapper objectMapper;

    public WebSocketConfig(FornoWebSocketHandler fornoWebSocketHandler,  JwtHandshakeInterceptor jwtHandshakeInterceptor, ObjectMapper objectMapper) {
        this.fornoWebSocketHandler = fornoWebSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println(">>> REGISTRANDO WEBSOCKET /v1/ws/fornos");
        //Mapeia a URL e conecta com o nosso Handler
        registry.addHandler(fornoWebSocketHandler, "/ws/*/fornos").addInterceptors(jwtHandshakeInterceptor).setAllowedOrigins("*");
    }
}
