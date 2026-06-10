package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.dto.TemporizadorRequestDTO;
import com.rafael.monitor_forno.dto.TemporizadorResponseDTO;
import com.rafael.monitor_forno.service.TemporizadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping
    public ResponseEntity<Void> criarTemporizador(@RequestBody TemporizadorRequestDTO dto, Authentication authentication) {

        temporizadorService.criarTemporizador(dto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTemporizador(@PathVariable UUID id) {
        temporizadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemporizadorResponseDTO> editarTemporizador(@RequestBody TemporizadorRequestDTO dto, @PathVariable UUID id) {
        return ResponseEntity.ok(temporizadorService.atualizarTemporizador(dto, id));
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Void> executarTemporizador(@PathVariable UUID id) {
        temporizadorService.marcarComoExecutado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proximo")
    public ResponseEntity<TemporizadorResponseDTO>
    buscarProximoTemporizador(
            Authentication authentication) {

        return ResponseEntity.ok(
                temporizadorService.buscarProximoTemporizador(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/meus")
    public ResponseEntity<List<TemporizadorResponseDTO>> buscarTemporizadorUsuario(Authentication authentication) {

        return ResponseEntity.ok(temporizadorService.buscarTemporizadoresUsuario(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<?> getTemporizador(@RequestParam(required = false) UUID id) {

        if (id != null) {
            return ResponseEntity.ok(temporizadorService.findById(id));
        }

        return ResponseEntity.ok(temporizadorService.findAll());
    }
}
