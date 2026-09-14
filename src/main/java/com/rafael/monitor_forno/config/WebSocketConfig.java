package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.handler.FornoWebSocketHandler;
import io.jsonwebtoken.JwtHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket //Ativa suporte de websocket para spring boot
public class WebSocketConfig implements WebSocketConfigurer {

    private final FornoWebSocketHandler fornoWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(FornoWebSocketHandler fornoWebSocketHandler,  JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.fornoWebSocketHandler = fornoWebSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println(">>> REGISTRANDO WEBSOCKET /ws/fornos");
        //Mapeia a URL e conecta com o nosso Handler
        registry.addHandler(fornoWebSocketHandler, "/ws/fornos").addInterceptors(jwtHandshakeInterceptor).setAllowedOrigins("*");
    }
}
