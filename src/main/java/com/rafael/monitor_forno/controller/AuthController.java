package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.database.model.RefreshToken;
import com.rafael.monitor_forno.database.repository.RefreshTokenRepository;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.service.RefreshTokenService;
import com.rafael.monitor_forno.service.UsuarioService;
import jakarta.validation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final RefreshTokenService  refreshTokenService;

    public AuthController(
            UsuarioService usuarioService, RefreshTokenService refreshTokenService) {

        this.usuarioService = usuarioService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto) {

        LoginResponseDTO response = usuarioService.login(dto.getEmail(), dto.getSenha());


        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO dto) {
        refreshTokenService.logout(dto.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/renovar-refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> renovar(@RequestBody RefreshTokenRequestDTO dto) {
        RefreshTokenResponseDTO response = refreshTokenService.renovarRefreshToken(dto.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/esqueci-minha-senha")
    public ResponseEntity<String> esqueciSenha(
            @RequestBody UserRequestDTO dto
            ) {
        usuarioService.gerarTokenRecuperacao(dto.getEmail());

        return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para recuperação.");
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody NovaSenhaDTO dto) {
        usuarioService.redefinirSenha(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/enviar-codigo-redefinir-email")
    public ResponseEntity<String> enviarCodigoRedefinirEmail(@Valid @RequestBody SolicitarTrocaEmailDTO dto, java.security.Principal principal) {

        String emailAtual = principal.getName();
        usuarioService.enviarCodigoRedefinirEmail (emailAtual, dto.senhaAtual(), dto.novoEmail());
        return ResponseEntity.ok("Código de redefinição enviado com sucesso.");
    }

    @PostMapping("/verificar-codigo-redefinir-email")
    public ResponseEntity<String> verificarCodigoRedefinirEmail(@RequestBody ConfirmarTrocaEmailDTO dto, java.security.Principal principal) {
        String emailAtual = principal.getName();
        usuarioService.verificarCodigoRedefinirEmail(emailAtual, dto.codigo());
        return ResponseEntity.ok("E-mail alterado com sucesso.");
    }

    @PutMapping("/reverter-email")
    public ResponseEntity<Void> reverterMudancaEmail(@RequestBody ReversaoEmailDTO dto) {

        usuarioService.reverterEmail(dto);
        return ResponseEntity.ok().build();

    }
}
