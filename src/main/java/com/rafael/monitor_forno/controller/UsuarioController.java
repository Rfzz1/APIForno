package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.NovaSenhaLogadoDTO;
import com.rafael.monitor_forno.dto.UserRequestDTO;
import com.rafael.monitor_forno.dto.UserResponseDTO;
import com.rafael.monitor_forno.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarUsuario(@Valid @RequestBody UserRequestDTO dto) {
        usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deletarUsuario(Authentication authentication) {
       usuarioService.deleteByEmail(authentication.getName());
       return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizarUsuario(@Valid @RequestBody UserRequestDTO dto, Authentication authentication) {
        UserResponseDTO usuarioAtualizado =
                usuarioService.atualizarUsuario(dto, authentication.getName());

        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping ("/{id}/promover")
    public ResponseEntity<UserResponseDTO> promoverUsuario(@PathVariable UUID id) {

        return ResponseEntity.ok(usuarioService.promoverUsuario(id));

    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/meu-perfil")
    public ResponseEntity<UserResponseDTO> getMeuPerfil(Authentication authentication) {
        UserResponseDTO usuario = usuarioService.meuPerfil(authentication.getName());
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/alterar-minha-senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody NovaSenhaLogadoDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        usuarioService.atualizarSenha(dto, email);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        return ResponseEntity.ok(usuarioService.findAll());

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUsuarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

}
