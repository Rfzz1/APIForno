package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TelemetriaResponseDTO;
import com.rafael.monitor_forno.service.TelemetriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/telemetrias")
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @PostMapping
    public ResponseEntity<Void> registrarTelemetria(@RequestBody TelemetriaRequestDTO dto, Authentication authentication) {

        telemetriaService.registrar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/atual")
    public ResponseEntity<TelemetriaResponseDTO> buscarAtual(Authentication authentication) {
        return ResponseEntity.ok(telemetriaService.buscarAtual(authentication.getName()));
    }
}
