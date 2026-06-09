package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.service.TelemetriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/telemetrias")
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @PostMapping
    public ResponseEntity<Void> registrarTelemetria(@RequestBody TelemetriaRequestDTO dto) {

        telemetriaService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
