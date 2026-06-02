package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.EventoDTO;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<Void> registrarEvento(
            @RequestBody EventoRequestDTO dto) {

        eventoService.registrarEvento(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<EventoDTO>> getAllEventos() {
        return ResponseEntity.ok(eventoService.findAll());
    }

}
