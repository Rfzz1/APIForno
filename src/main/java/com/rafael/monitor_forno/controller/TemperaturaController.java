package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.service.FornoDetailsService;
import com.rafael.monitor_forno.service.TemperaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/temperaturas")
public class TemperaturaController {

    private final TemperaturaService temperaturaService;

    public TemperaturaController(TemperaturaService temperaturaService) {
        this.temperaturaService = temperaturaService;
    }

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping
    public ResponseEntity<Void> registrarLeitura(@RequestBody TemperaturaRequestDTO dto, Authentication authentication) {

        FornoDetails fornoDetails = (FornoDetails) authentication.getPrincipal();

        dto.setFornoId(fornoDetails.getId());

        boolean salva = temperaturaService.registrarLeitura(dto, fornoDetails.getId());

        return salva ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLeitura(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        temperaturaService.deleteById(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/minhas")
    public ResponseEntity<List<TemperaturaDTO>> minhasTemperaturas(Authentication authentication) {
        return ResponseEntity.ok(temperaturaService.findAllByFornoUsuario(authentication.getName()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<?> buscarTemperaturas(@RequestParam(required = false) LocalDateTime registradoEm) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (registradoEm != null) {
            return ResponseEntity.ok(temperaturaService.findByDate(registradoEm));
        }
        return ResponseEntity.ok(temperaturaService.findAllByFornoUsuario(email));
    }
}
