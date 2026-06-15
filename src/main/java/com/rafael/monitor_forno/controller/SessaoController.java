package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.service.SessaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<SessaoResumoDTO> criarSessao(Authentication authentication) {

        SessaoResumoDTO sessao = sessaoService.iniciarSessao(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessao);
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<SessaoDetalhesDTO> encerrarSessao(@PathVariable UUID id, Authentication authentication) {

        SessaoDetalhesDTO sessao = sessaoService.encerrarSessao(id, authentication.getName());

        return ResponseEntity.ok(sessao);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSessao(@PathVariable UUID id, Authentication authentication) {
        sessaoService.deleteById(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<SessaoDetalhesDTO>> minhasSessao(Authentication authentication, @RequestParam(required = false) LocalDateTime dataInicio, @RequestParam(required = false) LocalDateTime dataFim) {

        return ResponseEntity.ok(sessaoService.findAllSessoesByUsuarioAndInicioSessaoBetween(authentication.getName(), dataInicio, dataFim));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SessaoResumoDTO>> pegarTodasSessoes() {

        return ResponseEntity.ok(
                sessaoService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessaoDetalhesDTO> pegarSessaoPorId(@PathVariable UUID id, Authentication authentication) {

        return ResponseEntity.ok(
                sessaoService.findById(id, authentication.getName())
        );

    }

}
