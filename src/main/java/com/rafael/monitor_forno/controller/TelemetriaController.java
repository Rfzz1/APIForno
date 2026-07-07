package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.DashboardDTO;
import com.rafael.monitor_forno.dto.EstatisticasDTO;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TelemetriaResponseDTO;
import com.rafael.monitor_forno.service.TelemetriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/telemetrias")
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping
    public ResponseEntity<Void> registrarTelemetria(@RequestBody TelemetriaRequestDTO dto) {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        telemetriaService.registrar(dto, serialNumber);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/forno/{fornoId}/atual")
    public ResponseEntity<TelemetriaResponseDTO> buscarTelemetriaAtual(@PathVariable UUID fornoId) {
        // O Usuário se identifica pelo token (E-mail)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(telemetriaService.buscarAtual(fornoId, email));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/forno/{fornoId}/dashboard")
    public ResponseEntity<DashboardDTO> buscarDashboard(@PathVariable UUID fornoId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(telemetriaService.buscarDashboard(fornoId, email));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/estatisticas")
    public ResponseEntity<EstatisticasDTO> buscarEstatisticas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // A estatística continua global (soma dos dados de todos os fornos do usuário)
        return ResponseEntity.ok(telemetriaService.buscarEstatisticas(email));
    }
}
