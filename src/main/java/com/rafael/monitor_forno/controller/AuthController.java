package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.LoginRequestDTO;
import com.rafael.monitor_forno.dto.LoginResponseDTO;
import com.rafael.monitor_forno.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto) {

        String token = usuarioService.login(
                dto.getEmail(),
                dto.getSenha()
        );

        return ResponseEntity.ok(
                new LoginResponseDTO(token)
        );
    }
}
