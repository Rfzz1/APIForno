package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.service.UsuarioService;
import jakarta.validation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto) {

        LoginResponseDTO response = usuarioService.login(dto.getEmail(), dto.getSenha());

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
}
