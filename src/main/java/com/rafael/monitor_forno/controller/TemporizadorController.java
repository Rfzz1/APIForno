package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.TemporizadorRequestDTO;
import com.rafael.monitor_forno.dto.TemporizadorResponseDTO;
import com.rafael.monitor_forno.service.TemporizadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/temporizadores")
public class TemporizadorController {

    private final TemporizadorService temporizadorService;

    public TemporizadorController(TemporizadorService temporizadorService) {
        this.temporizadorService = temporizadorService;
    }

    // ========= ROTAS COMPARTILHADAS =======

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/forno/{fornoId}")
    public ResponseEntity<Void> criarTemporizador(@Valid @RequestBody TemporizadorRequestDTO dto, @PathVariable UUID fornoId, Authentication authentication) {

        temporizadorService.criarTemporizador(dto, fornoId, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTemporizador(@PathVariable UUID id, Authentication authentication) {
        temporizadorService.deleteById(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TemporizadorResponseDTO> editarTemporizador(@Valid @RequestBody TemporizadorRequestDTO dto, @PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(temporizadorService.atualizarTemporizador(dto, id, authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/meus")
    public ResponseEntity<List<TemporizadorResponseDTO>> buscarTemporizadorUsuario(Authentication authentication) {

        return ResponseEntity.ok(temporizadorService.buscarTemporizadoresUsuario(authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/fornos/{fornoId}")
    public ResponseEntity<List<TemporizadorResponseDTO>> getAllTemporizadoresByFornoIdAndUsuario(@PathVariable UUID fornoId, Authentication authentication) {

        return ResponseEntity.ok(temporizadorService.buscarTemporizadoresFornoUsuario(fornoId, authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<?> getTemporizador(@RequestParam(required = false) UUID id, Authentication authentication) {

        if (id != null) {
            return ResponseEntity.ok(temporizadorService.findById(id, authentication.getName()));
        }

        return ResponseEntity.ok(temporizadorService.findAllByFornoUsuario(authentication.getName()));
    }

    // ========= ROTAS DO FORNO =======

    @PreAuthorize("hasRole('FORNO')")
    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Void> executarTemporizador(@PathVariable UUID id, Authentication authentication) {

        UUID fornoId = ((FornoDetails) authentication.getPrincipal()).getId();

        temporizadorService.marcarComoExecutado(id, fornoId);
        return ResponseEntity.noContent().build();
    }
}
