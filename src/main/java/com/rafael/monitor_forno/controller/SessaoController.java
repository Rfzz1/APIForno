package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.dto.SessaoAtualizarDTO;
import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoEncerrarDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.service.SessaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    // ========= ROTAS DO FORNO =======

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping("/iniciar")
    public ResponseEntity<SessaoResumoDTO> criarSessao(Authentication authentication) {

        UUID fornoId = ((FornoDetails) authentication.getPrincipal()).getId();

        SessaoResumoDTO sessao = sessaoService.iniciarSessao(fornoId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessao);
    }

    @PreAuthorize("hasRole('FORNO')")
    @PutMapping("/{id}/encerrar")
    public ResponseEntity<SessaoDetalhesDTO> encerrarSessao(@PathVariable UUID id, @Valid @RequestBody SessaoEncerrarDTO dto, Authentication authentication) {

        UUID fornoId = ((FornoDetails) authentication.getPrincipal()).getId();
        SessaoDetalhesDTO sessao = sessaoService.encerrarSessao(id, fornoId, dto);

        return ResponseEntity.ok(sessao);

    }

    @PreAuthorize("hasRole('FORNO')")
    @PutMapping("/{id}/atualizar")
    public ResponseEntity<SessaoDetalhesDTO> atualizarSessao(@PathVariable UUID id, @Valid @RequestBody SessaoAtualizarDTO dto, Authentication authentication) {

        UUID fornoId = ((FornoDetails) authentication.getPrincipal()).getId();
        SessaoDetalhesDTO sessao = sessaoService.atualizarSessao(id, fornoId, dto);

        return ResponseEntity.ok(sessao);
    }

    // ========= ROTAS COMPARTILHADAS =======

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSessao(@PathVariable UUID id, Authentication authentication) {
        sessaoService.deleteById(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/minhas")
    public ResponseEntity<List<SessaoResumoDTO>> minhasSessao(Authentication authentication, @RequestParam(required = false) Instant dataInicio, @RequestParam(required = false) Instant dataFim) {

        return ResponseEntity.ok(sessaoService.findAllSessoesByUsuarioAndInicioSessaoBetween(authentication.getName(), dataInicio, dataFim));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SessaoDetalhesDTO> pegarSessaoPorId(@PathVariable UUID id, Authentication authentication) {

        return ResponseEntity.ok(
                sessaoService.findById(id, authentication.getName())
        );

    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/fornos/{fornoId}")
    public ResponseEntity<List<SessaoResumoDTO>> getAllSessoesByFornoIdAndUsuario(@PathVariable UUID fornoId, Authentication authentication) {

        return ResponseEntity.ok(sessaoService.findAllSessoesByFornoIdAndUsuario(fornoId, authentication.getName()));

    }

    // ========= ROTAS DO ADMIN =======

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SessaoResumoDTO>> pegarTodasSessoes() {

        return ResponseEntity.ok(
                sessaoService.findAll()
        );
    }

}
