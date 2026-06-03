package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.TemporizadorRequestDTO;
import com.rafael.monitor_forno.service.TemporizadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/temporizador")
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
}
