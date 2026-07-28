package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.handler.FornoWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket //Ativa suporte de websocket para spring boot
public class WebSocketConfig implements WebSocketConfigurer {

    private final FornoWebSocketHandler fornoWebSocketHandler;

    public WebSocketConfig(FornoWebSocketHandler fornoWebSocketHandler) {
        this.fornoWebSocketHandler = fornoWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //Mapeia a URL e conecta com o nosso Handler
        registry.addHandler(fornoWebSocketHandler, "/ws/forno").setAllowedOrigins("*");
    }
}
