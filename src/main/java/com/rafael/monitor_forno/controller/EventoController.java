package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.EventoDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<Void> registrarEvento(
            @RequestBody EventoRequestDTO dto, Authentication authentication) {

        eventoService.registrarEvento(dto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(
            @PathVariable UUID id) {

        eventoService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllEventos(@RequestParam(required = false) UUID id) {

        if (id != null) {
            return ResponseEntity.ok(eventoService.findById(id));
        }

        return ResponseEntity.ok(eventoService.findAll());
    }

}
