package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.service.FornoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/fornos")
public class FornoController {

    private final FornoService fornoService;

    public FornoController(FornoService fornoService) {
        this.fornoService = fornoService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/fabricar")
    public ResponseEntity<FornoResponseDTO> fabricarForno(@Valid @RequestBody FabricarFornoDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(fornoService.fabricar(dto));
    }

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseDTO> autenticar(@Valid @RequestBody FornoAuthDTO dto) {

        return ResponseEntity.ok(fornoService.autenticar(dto));
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/vincular")
    public ResponseEntity<Void> vincularForno(@Valid @RequestBody VincularFornoDTO dto, Authentication authentication) {
        fornoService.vincularFornoaoUsuario(dto, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/meus")
    public ResponseEntity<List<FornoResponseDTO>> buscarMeusFornos(Authentication authentication) {
        List<FornoResponseDTO> fornos = fornoService.buscarMeusFornos(authentication.getName());
        return ResponseEntity.ok(fornos);
    }

}
