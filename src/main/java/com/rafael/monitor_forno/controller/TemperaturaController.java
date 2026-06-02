package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.service.TemperaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("v1/temperaturas")
public class TemperaturaController {

    private final TemperaturaService temperaturaService;

    public TemperaturaController(TemperaturaService temperaturaService) {
        this.temperaturaService = temperaturaService;
    }

    @PostMapping
    public ResponseEntity<Void> registrarLeitura(@RequestBody TemperaturaRequestDTO dto) {

        boolean salva = temperaturaService.registrarLeitura(dto);

        if (!salva) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping
    public ResponseEntity<?> buscarTemperaturas(
            @RequestParam(required = false) LocalDateTime registradoEm) {

        if (registradoEm != null) {
            return ResponseEntity.ok(
                    temperaturaService.findByDate(registradoEm));
        }

        return ResponseEntity.ok(
                temperaturaService.findAll());
    }
}
