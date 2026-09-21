package com.rafael.monitor_forno.service;

import ch.qos.logback.classic.Logger;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.exception.SessaoEncerradaException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.expirationRefresh}")
    private Long expirationRefresh;

    private static final String TIPO = "tipo";
    private static final String ROLE = "role";
    private static final String VERSAO_USER = "versao";
    private static final String EMAIL_ANTIGO = "emailAntigo";
    private static final String NOVO_EMAIL = "novoEmail";

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Secret + header + payload (Agora exige o tipo da entidade)
    public String gerarToken(String subject, String tipo, String role, Long versaoUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TIPO, tipo);
        claims.put(ROLE, role);
        claims.put(VERSAO_USER, versaoUsuario);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 24 horas
                .signWith(getSecretKey())
                .compact();
    }

    public String gerarToken(String subject, String tipo, String role) {
        return gerarToken(subject, tipo, role, null);
    }

    public String gerarRefreshToken(String subject, String tipo, String role, Long versaoUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TIPO, tipo);
        claims.put(ROLE, role);
        claims.put(VERSAO_USER, versaoUsuario);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationRefresh)) // 2 semanas
                .signWith(getSecretKey())
                .compact();
    }

    public String gerarTokenReversaoEmail(String emailAntigo, String novoEmail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TIPO, "ReversaoEmail");
        claims.put(EMAIL_ANTIGO, emailAntigo);
        claims.put(NOVO_EMAIL, novoEmail);

        return Jwts.builder()
                .claims(claims)
                .subject(emailAntigo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
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

        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            String tokenSubject = extrairSubject(token);
            return Objects.equals(tokenSubject, username);
        } catch (JwtException e) {
            log.warn("Token Inválido: {}", e.getMessage());
            return false;
        }
    }
}