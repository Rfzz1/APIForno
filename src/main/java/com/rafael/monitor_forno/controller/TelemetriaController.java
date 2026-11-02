package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.DashboardDTO;
import com.rafael.monitor_forno.dto.EstatisticasDTO;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TelemetriaResponseDTO;
import com.rafael.monitor_forno.service.TelemetriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/telemetrias")
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    // ========= ROTAS DO FORNO =======

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping
    public ResponseEntity<Void> registrarTelemetria(@Valid @RequestBody TelemetriaRequestDTO dto, Authentication authentication) {

        UUID fornoId = ((FornoDetails) authentication.getPrincipal()).getId();

        telemetriaService.registrar(dto, fornoId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ========= ROTAS COMPARTILHADAS =======

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/forno/{fornoId}/atual")
    public ResponseEntity<TelemetriaResponseDTO> buscarTelemetriaAtual(@PathVariable UUID fornoId, Authentication authentication) {

        return ResponseEntity.ok(telemetriaService.buscarAtual(fornoId, authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/forno/{fornoId}/dashboard")
    public ResponseEntity<DashboardDTO> buscarDashboard(@PathVariable UUID fornoId, Authentication authentication) {

        return ResponseEntity.ok(telemetriaService.buscarDashboard(fornoId, authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/forno/{fornoId}/estatisticas")
    public ResponseEntity<EstatisticasDTO> buscarEstatisticas(@PathVariable UUID fornoId, Authentication authentication) {

        // A estatística continua global (soma dos dados de todos os fornos do usuário)
        return ResponseEntity.ok(telemetriaService.buscarEstatisticas(fornoId, authentication.getName()));
    }
}
