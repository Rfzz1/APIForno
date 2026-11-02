package com.rafael.monitor_forno.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.dto.EventoResponseDTO;
import com.rafael.monitor_forno.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // ========= ROTAS DO FORNO =======

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping
    public ResponseEntity<Void> registerEvent(@Valid @RequestBody EventoRequestDTO dto, Authentication authentication) throws FirebaseMessagingException {

        FornoDetails fornoDetails = (FornoDetails) authentication.getPrincipal();

        dto.setFornoId(Objects.requireNonNull(fornoDetails).getId());

        eventoService.registrarEvento(dto, fornoDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    // ========= ROTAS COMPARTILHADAS =======

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id, Authentication authentication) {

        eventoService.deleteById(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/fornos/{fornoId}")
    public ResponseEntity<List<EventoResponseDTO>> getAllEventsByOvenIdAndUser(@PathVariable UUID fornoId, Authentication authentication) {

        return ResponseEntity.ok(eventoService.findAllByFornoAndUsuario(fornoId, authentication.getName()));
    }


    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllEvents(@RequestParam(required = false) UUID id, Authentication authentication) {

        if (id != null) {
            return ResponseEntity.ok(eventoService.findById(id, authentication.getName()));
        }

        return ResponseEntity.ok(eventoService.findAllByFornoUsuario(authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/fornos/{fornoId}/ultimos-tres")
    public ResponseEntity<List<EventoResponseDTO>> getLastThreeEventsByOvenId(@PathVariable UUID fornoId, Authentication authentication) {
        return ResponseEntity.ok(eventoService.findLastThreeByFornoAndUsuario(fornoId, authentication.getName()));
    }

}
