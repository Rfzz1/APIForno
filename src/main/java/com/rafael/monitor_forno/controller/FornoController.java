package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.service.FornoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fornos")
public class FornoController {

    private final FornoService fornoService;

    public FornoController(FornoService fornoService) {
        this.fornoService = fornoService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/pre-registrar")
    public ResponseEntity<FornoResponseDTO> fabricarForno(@Valid @RequestBody PreRegistroFornoDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(fornoService.preRegistro(dto));
    }

    @PostMapping("/auto-provisionar")
    public ResponseEntity<AutoProvisionamentoResponseDTO> autoProvisionar(@Valid @RequestBody AutoProvisionamentoDTO dto) {

        return ResponseEntity.ok(fornoService.autoProvisionar(dto));
    }

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseDTO> autenticar(@Valid @RequestBody FornoAuthDTO dto) {

        return ResponseEntity.ok(fornoService.autenticar(dto));
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @PutMapping("/atualizar-forno")
    public ResponseEntity<FornoResponseDTO> atualizarForno(@Valid @RequestBody FornoAtualizarDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        FornoResponseDTO forno = fornoService.atualizarForno(dto, email, serialNumber);
        return ResponseEntity.ok(forno);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/vincular")
    public ResponseEntity<Void> vincularForno(@Valid @RequestBody VincularFornoDTO dto, Authentication authentication) {
        fornoService.vincularFornoaoUsuario(dto, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/meus")
    public ResponseEntity<List<FornoResponseDTO>> buscarMeusFornos(Authentication authentication) {
        List<FornoResponseDTO> fornos = fornoService.buscarMeusFornos(authentication.getName());
        return ResponseEntity.ok(fornos);
    }

}
