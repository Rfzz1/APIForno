package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.service.FornoDetailsService;
import com.rafael.monitor_forno.service.TemperaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping
    public ResponseEntity<Void> registrarLeitura(@RequestBody TemperaturaRequestDTO dto, Authentication authentication) {

        FornoDetails fornoDetails = (FornoDetails) authentication.getPrincipal();

        dto.setFornoId(fornoDetails.getId());

        boolean salva = temperaturaService.registrarLeitura(dto, fornoDetails.getId());

        return salva ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLeitura(@PathVariable UUID id, Authentication authentication) {
        temperaturaService.deleteById(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<TemperaturaDTO>> minhasTemperaturas(Authentication authentication) {
        return ResponseEntity.ok(temperaturaService.findAllByUsuario(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<?> buscarTemperaturas(
            @RequestParam(required = false) LocalDateTime registradoEm, Authentication authentication) {

        if (registradoEm != null) {
            return ResponseEntity.ok(
                    temperaturaService.findByDate(registradoEm));
        }

        return ResponseEntity.ok(
                temperaturaService.findAllByUsuario(authentication.getName()));
    }
}
