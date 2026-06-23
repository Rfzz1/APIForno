package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.dto.TemporizadorRequestDTO;
import com.rafael.monitor_forno.dto.TemporizadorResponseDTO;
import com.rafael.monitor_forno.service.TemporizadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/temporizadores")
public class TemporizadorController {

    private final TemporizadorService temporizadorService;

    public TemporizadorController(TemporizadorService temporizadorService) {
        this.temporizadorService = temporizadorService;
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/forno/{fornoId}")
    public ResponseEntity<Void> criarTemporizador(@RequestBody TemporizadorRequestDTO dto, @PathVariable UUID fornoId, Authentication authentication) {

        temporizadorService.criarTemporizador(dto, fornoId, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTemporizador(@PathVariable UUID id, Authentication authentication) {
        temporizadorService.deleteById(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<TemporizadorResponseDTO> editarTemporizador(@RequestBody TemporizadorRequestDTO dto, @PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(temporizadorService.atualizarTemporizador(dto, id, authentication.getName()));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/meus")
    public ResponseEntity<List<TemporizadorResponseDTO>> buscarTemporizadorUsuario(Authentication authentication) {

        return ResponseEntity.ok(temporizadorService.buscarTemporizadoresUsuario(authentication.getName()));
    }

    @PreAuthorize("hasRole('FORNO')")
    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Void> executarTemporizador(@PathVariable UUID id) {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        temporizadorService.marcarComoExecutado(id, serialNumber);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('FORNO')")
    @GetMapping("/proximo")
    public ResponseEntity<TemporizadorResponseDTO>
    buscarProximoTemporizador() {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                temporizadorService.buscarProximoTemporizador(
                        serialNumber
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getTemporizador(@RequestParam(required = false) UUID id, Authentication authentication) {

        if (id != null) {
            return ResponseEntity.ok(temporizadorService.findById(id, authentication.getName()));
        }

        return ResponseEntity.ok(temporizadorService.findAll());
    }
}
