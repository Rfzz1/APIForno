package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.RefreshToken;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.RefreshTokenRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.RefreshTokenResponseDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public RefreshToken cadastrarRefreshToken(String email) {
        Usuario usuario = buscarUsuarioLogado(email);

        RefreshToken refreshToken = new RefreshToken();

        String refreshTokenUsuario = jwtService.gerarRefreshToken(usuario.getEmail(), "USUARIO", "USUARIO", usuario.getVersaoUsuario());

        Duration duracaoexpiracao = Duration.ofMillis(expirationRefresh);
        LocalDateTime expiracao =  LocalDateTime.now().plusMinutes(duracaoexpiracao.toMinutes());

        refreshToken.setUsuario(usuario);
        refreshToken.setRefreshToken(refreshTokenUsuario);
        refreshToken.setExpiracaoRefreshToken(expiracao);
        return refreshTokenRepository.save(refreshToken);

    }

    public RefreshTokenResponseDTO renovarRefreshToken(String refreshToken) {

        Claims claims = jwtService.extrairTodasClaims(refreshToken);
        String subject = claims.getSubject();

        RefreshToken tokenDoBanco = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Refresh token não encontrado"
                        )
                );

        if (tokenDoBanco.getExpiracaoRefreshToken().isBefore(java.time.LocalDateTime.now())) {
            throw new RecursoNaoEncontradoException("Refresh token expirado");
        }

        if (!tokenDoBanco.getUsuario().getEmail().equals(subject)) {
            throw new AcessoNegadoException("Usuário não correspondente com a sessão");
        }

        return toRefreshToken(tokenDoBanco);

    }

    @Transactional
    public void logout(String refreshToken) {

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public RefreshTokenResponseDTO toRefreshToken(RefreshToken refreshToken) {
        return RefreshTokenResponseDTO.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .expiration(refreshToken.getExpiracaoRefreshToken())
                .novoToken(jwtService.gerarToken(refreshToken.getUsuario().getEmail(), "USUARIO", "USUARIO"))
                .build();
    }
}
