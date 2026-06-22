package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping
    public ResponseEntity<Void> registrarEvento(
            @RequestBody EventoRequestDTO dto) {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        eventoService.registrarEvento(dto, serialNumber);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(
            @PathVariable UUID id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        eventoService.deleteById(id, email);

        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<?> getAllEventos(@RequestParam(required = false) UUID id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (id != null) {
            return ResponseEntity.ok(eventoService.findById(id, email));
        }

        return ResponseEntity.ok(eventoService.findAllByFornoUsuario(email));
    }

}
