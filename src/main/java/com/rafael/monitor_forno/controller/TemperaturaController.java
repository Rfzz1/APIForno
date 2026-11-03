package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.service.TemperaturaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/temperaturas")
public class TemperaturaController {

    private final TemperaturaService temperaturaService;

    public TemperaturaController(TemperaturaService temperaturaService) {
        this.temperaturaService = temperaturaService;
    }

    // ========= ROTAS DO FORNO =======

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping
    public ResponseEntity<Void> registerTemperature(@Valid @RequestBody TemperaturaRequestDTO dto, Authentication authentication) {

        FornoDetails fornoDetails = (FornoDetails) authentication.getPrincipal();

        dto.setFornoId(fornoDetails.getId());

        boolean salva = temperaturaService.registrarLeitura(dto, fornoDetails.getId());

        log.info("Temperatura registrada para o forno: {}", dto.getFornoId());
        return salva ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.noContent().build();
    }

    // ========= ROTAS COMPARTILHADAS =======

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/fornos/{fornoId}/temperaturas/{id}")
    public ResponseEntity<Void> deleteTemperature(@PathVariable UUID id, @PathVariable UUID fornoId, Authentication authentication) {
        temperaturaService.deleteById(id, fornoId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/fornos/{fornoId}")
    public ResponseEntity<List<TemperaturaDTO>> getAllTemperaturesByOvenIdAndUser(@PathVariable UUID fornoId, Authentication authentication) {

        return ResponseEntity.ok(temperaturaService.findAllByFornoIdAndUsuario(fornoId, authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/minhas")
    public ResponseEntity<List<TemperaturaDTO>> myTemperatures(Authentication authentication) {
        return ResponseEntity.ok(temperaturaService.findAllByFornoUsuario(authentication.getName()));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/sessoes/{sessaoId}/temperaturas")
    public ResponseEntity<List<TemperaturaDTO>> searchTemperaturesBySession(@PathVariable UUID sessaoId, Authentication authentication) {
        List<TemperaturaDTO> temperaturas = temperaturaService.buscarTemperaturasPorSessao(sessaoId, authentication.getName());
        return ResponseEntity.ok(temperaturas);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<?> searchTemperatures(@RequestParam(required = false) Instant createdAt, @RequestParam(required = false) UUID ovenId, Authentication authentication) {

        if (createdAt != null && ovenId == null) {
            throw new CredenciaisInvalidasException("Os parâmetros são obrigatórios: createdAt e ovenId devem ser fornecidos juntos.");
        }

        if (ovenId != null && createdAt != null) {
            return ResponseEntity.ok(temperaturaService.findByDate(createdAt, ovenId, authentication.getName()));
        }

        return ResponseEntity.ok(temperaturaService.findAllByFornoUsuario(authentication.getName()));
    }
}
