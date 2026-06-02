package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.dto.UserRequestDTO;
import com.rafael.monitor_forno.dto.UserResponseDTO;
import com.rafael.monitor_forno.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarUsuario(@RequestBody UserRequestDTO dto) {
        usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id) {
       usuarioService.deleteById(id);
       return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizarUsuario(@RequestBody UserRequestDTO dto, @PathVariable UUID id) {
        UserResponseDTO usuarioAtualizado =
                usuarioService.atualizarUsuario(dto, id);

        return ResponseEntity.ok(usuarioAtualizado);
    }

    @GetMapping
    public ResponseEntity<?> getUsuario(@RequestParam(required = false) UUID id) {
        if (id != null) {
            return ResponseEntity.ok(usuarioService.findById(id));
        }

        return ResponseEntity.ok(usuarioService.findAll());

    }

}
