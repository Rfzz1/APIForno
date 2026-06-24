package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.LoginRequestDTO;
import com.rafael.monitor_forno.dto.LoginResponseDTO;
import com.rafael.monitor_forno.dto.NovaSenhaDTO;
import com.rafael.monitor_forno.dto.UserRequestDTO;
import com.rafael.monitor_forno.service.EmailService;
import com.rafael.monitor_forno.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins="http://localhost:5173")
public class    AuthController {

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
}
