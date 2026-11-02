package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.FotoPerfilRequestDTO;
import com.rafael.monitor_forno.dto.FotoPerfilResponseDTO;
import com.rafael.monitor_forno.service.FotoPerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuario/foto-perfil")
public class FotoPerfilController {

    private final FotoPerfilService fotoPerfilService;

    public FotoPerfilController(FotoPerfilService fotoPerfilService) {
        this.fotoPerfilService = fotoPerfilService;
    }

    // ========= ROTAS COMPARTILHADAS =======

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/set-img")
    public ResponseEntity<FotoPerfilResponseDTO> setProfileImage(@Valid @RequestBody FotoPerfilRequestDTO dto, Authentication authentication) {

        FotoPerfilResponseDTO responseDTO = fotoPerfilService.setProfileImage(dto, authentication.getName());
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/update-img")
    public ResponseEntity<FotoPerfilResponseDTO> updateProfileImage(@Valid @RequestBody FotoPerfilRequestDTO dto, Authentication authentication) {

        FotoPerfilResponseDTO responseDTO = fotoPerfilService.changeProfileImage(dto, authentication.getName());
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/delete-img")
    public ResponseEntity<Void> deleteProfileImage(Authentication authentication) {

        fotoPerfilService.deleteProfileImage(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/visualizar-img")
    public ResponseEntity<FotoPerfilResponseDTO> getProfileImage(Authentication authentication) {

        FotoPerfilResponseDTO responseDTO = fotoPerfilService.getFotoPerfilByUsuario(authentication.getName());
        return ResponseEntity.ok(responseDTO);
    }

    // ========= ROTAS DO ADMIN =======

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/visualizar-img/{id}")
    public ResponseEntity<FotoPerfilResponseDTO> getProfileImage(@PathVariable UUID id) {
        return ResponseEntity.ok(fotoPerfilService.findById(id));
    }

}
