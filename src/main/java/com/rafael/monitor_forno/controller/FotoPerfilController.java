package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.FotoPerfilRequestDTO;
import com.rafael.monitor_forno.dto.FotoPerfilResponseDTO;
import com.rafael.monitor_forno.service.FotoPerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/usuario/foto-perfil")
public class FotoPerfilController {

    private final FotoPerfilService fotoPerfilService;

    public FotoPerfilController(FotoPerfilService fotoPerfilService) {
        this.fotoPerfilService = fotoPerfilService;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/set-img")
    public ResponseEntity<FotoPerfilResponseDTO> setProfileImage(@RequestBody FotoPerfilRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        FotoPerfilResponseDTO responseDTO = fotoPerfilService.setProfileImage(dto, email);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/update-img")
    public ResponseEntity<FotoPerfilResponseDTO> updateProfileImage(@RequestBody FotoPerfilRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        FotoPerfilResponseDTO responseDTO = fotoPerfilService.changeProfileImage(dto, email);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/delete-img")
    public ResponseEntity<Void> deleteProfileImage() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        fotoPerfilService.deleteProfileImage(email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/visualizar-img")
    public ResponseEntity<FotoPerfilResponseDTO> getProfileImage() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        FotoPerfilResponseDTO responseDTO = fotoPerfilService.getFotoPerfilByUsuario(email);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/visualizar-img/{id}")
    public ResponseEntity<FotoPerfilResponseDTO> getProfileImage(@PathVariable UUID id) {
        return ResponseEntity.ok(fotoPerfilService.findById(id));
    }

}
