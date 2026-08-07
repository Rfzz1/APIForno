package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.SuporteNovaMensagemRequestDTO;
import com.rafael.monitor_forno.dto.SuporteResponseDTO;
import com.rafael.monitor_forno.service.SuporteService;
import jakarta.validation.*;
import com.rafael.monitor_forno.dto.SuporteRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/suporte")
public class SuporteController {

    private final SuporteService suporteService;

    public SuporteController(SuporteService suporteService) {
        this.suporteService = suporteService;
    }

    //========== USER =============

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Void> criarTicket(@Valid @RequestBody SuporteRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        suporteService.criarTicket(dto, email);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}/buscar-ticket")
    public ResponseEntity<SuporteResponseDTO> buscarTicket(@PathVariable UUID id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SuporteResponseDTO ticket = suporteService.buscarTicket(email, id);

        return ResponseEntity.ok(ticket);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/meus-tickets")
    public ResponseEntity<List<SuporteResponseDTO>> listarMeusTickets() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SuporteResponseDTO> tickets = suporteService.findAllBySuporteUsuario(email);

        return ResponseEntity.ok(tickets);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/{id}/mensagens")
    public ResponseEntity<Void> responderTicket(@PathVariable UUID id, @Valid @RequestBody SuporteNovaMensagemRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        suporteService.adicionarMensagemChat(id, dto, email);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{id}/deletar-ticket")
    public ResponseEntity<Void> deletarTicket(@PathVariable UUID id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        suporteService.deletarTicket(id, email);

        return ResponseEntity.noContent().build();
    }

    //=========== ADMIN =================

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/todos-tickets")
    public ResponseEntity<List<SuporteResponseDTO>> listarTodosTickets() {

        List<SuporteResponseDTO> tickets = suporteService.listarTickets();
        return ResponseEntity.ok(tickets);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}/finalizar-ticket")
    public ResponseEntity<Void> finalizarTicket(@PathVariable UUID id) {

        suporteService.finalizarticket(id);
        return ResponseEntity.ok().build();
    }

}
