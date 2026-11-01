package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.RefreshToken;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.RefreshTokenRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.RefreshTokenResponseDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.exception.SenhaHashEmFormatoInvalido;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Value("${jwt.expirationRefresh}")
    private Long expirationRefresh;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    private Usuario buscarUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
    }

    private String gerarHashSha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new SenhaHashEmFormatoInvalido("Erro ao gerar hash", e);
        }
    }

    public RefreshToken cadastrarRefreshToken(String email) {
        Usuario usuario = buscarUsuarioLogado(email);

        RefreshToken refreshToken = new RefreshToken();

        String refreshTokenUsuario = jwtService.gerarRefreshToken(usuario.getEmail(), "USUARIO", "USUARIO", usuario.getVersaoUsuario());

        String hash = gerarHashSha256(refreshTokenUsuario);

        Duration duracaoexpiracao = Duration.ofMillis(expirationRefresh);
        Instant expiracao =  Instant.now().plus(duracaoexpiracao);

        refreshToken.setUsuario(usuario);
        refreshToken.setRefreshToken(hash);
        refreshToken.setExpiracaoRefreshToken(expiracao);

        RefreshToken tokenSalvo = refreshTokenRepository.save(refreshToken);

        tokenSalvo.setRefreshToken(refreshTokenUsuario);
        return tokenSalvo;

    }

    public RefreshTokenResponseDTO renovarRefreshToken(String refreshToken) {

        Claims claims = jwtService.extrairTodasClaims(refreshToken);

        String hashTokenBanco = gerarHashSha256(refreshToken);

        if (!"REFRESH".equals(
                claims.get("tokenTipo", String.class))) {
            throw new AcessoNegadoException("Token inválido para renovação");
        }

        String subject = claims.getSubject();
        Long versaoToken = claims.get("versao", Long.class);

        RefreshToken tokenDoBanco = refreshTokenRepository.findByRefreshToken(hashTokenBanco)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Refresh token não encontrado"
                        )
                );

        if (tokenDoBanco.getExpiracaoRefreshToken().isBefore(java.time.Instant.now())) {
            throw new RecursoNaoEncontradoException("Refresh token expirado");
        }

        if (!tokenDoBanco.getUsuario().getEmail().equals(subject)) {
            throw new AcessoNegadoException("Usuário não correspondente com a sessão");
        }

        if (!tokenDoBanco.getUsuario().getVersaoUsuario().equals(versaoToken)) {
            throw new CredenciaisInvalidasException("Refresh Token não corresponde à versão do usuário");
        }

        refreshTokenRepository.delete(tokenDoBanco);

        RefreshToken novoRefreshToken = cadastrarRefreshToken(subject);

        return toRefreshToken(novoRefreshToken);

    }

    @Transactional
    public void logout(String refreshToken) {

        String hash = gerarHashSha256(refreshToken);

        refreshTokenRepository.deleteByRefreshToken(hash);
    }

    public RefreshTokenResponseDTO toRefreshToken(RefreshToken refreshToken) {
        return RefreshTokenResponseDTO.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .expiration(refreshToken.getExpiracaoRefreshToken())
                .novoToken(jwtService.gerarToken(refreshToken.getUsuario().getEmail(), "USUARIO", "USUARIO", refreshToken.getUsuario().getVersaoUsuario()))
                .build();
    }
}
