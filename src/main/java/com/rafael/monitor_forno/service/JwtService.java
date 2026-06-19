package com.rafael.monitor_forno.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Secret + header + payload (Agora exige o tipo da entidade)
    public String gerarToken(String subject, String tipo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tipo", tipo); // Salva se é "USUARIO" ou "FORNO"

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(getSecretKey()) // Corrigido para getSecretKey() diretamente
                .compact();
    }

    public Claims extrairTodasClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extrairSubject(String token) {
        return extrairTodasClaims(token).getSubject();
    }

    public boolean tokenValido(String token, String username) {
        try {
            String tokenSubject = extrairSubject(token);
            return tokenSubject.equals(username);
        } catch (Exception e) {
            return false;
        }
    }
}