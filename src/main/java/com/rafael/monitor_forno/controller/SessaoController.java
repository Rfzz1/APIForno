package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.SessaoAtualizarDTO;
import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoEncerrarDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.service.SessaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PreAuthorize("hasRole('FORNO')")
    @PostMapping("/iniciar")
    public ResponseEntity<SessaoResumoDTO> criarSessao() {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        SessaoResumoDTO sessao = sessaoService.iniciarSessao(serialNumber);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessao);
    }

    @PreAuthorize("hasRole('FORNO')")
    @PutMapping("/{id}/encerrar")
    public ResponseEntity<SessaoDetalhesDTO> encerrarSessao(@PathVariable UUID id, @RequestBody SessaoEncerrarDTO dto) {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        SessaoDetalhesDTO sessao = sessaoService.encerrarSessao(id, serialNumber, dto);

        return ResponseEntity.ok(sessao);

    }

    @PreAuthorize("hasRole('FORNO')")
    @PutMapping("/{id}/atualizar")
    public ResponseEntity<SessaoDetalhesDTO> atualizarSessao(@PathVariable UUID id, @RequestBody SessaoAtualizarDTO dto) {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        SessaoDetalhesDTO sessao = sessaoService.atualizarSessao(id, serialNumber, dto);

        return ResponseEntity.ok(sessao);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSessao(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        sessaoService.deleteById(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/minhas")
    public ResponseEntity<List<SessaoDetalhesDTO>> minhasSessao(Authentication authentication, @RequestParam(required = false) LocalDateTime dataInicio, @RequestParam(required = false) LocalDateTime dataFim) {

        return ResponseEntity.ok(sessaoService.findAllSessoesByUsuarioAndInicioSessaoBetween(authentication.getName(), dataInicio, dataFim));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SessaoDetalhesDTO> pegarSessaoPorId(@PathVariable UUID id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(
                sessaoService.findById(id, email)
        );

    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/fornos/{fornoId}")
    public ResponseEntity<List<SessaoDetalhesDTO>> getAllSessoesByFornoIdAndUsuario(@PathVariable UUID fornoId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(sessaoService.findAllSessoesByFornoIdAndUsuario(fornoId, email));

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SessaoResumoDTO>> pegarTodasSessoes() {

        return ResponseEntity.ok(
                sessaoService.findAll()
        );
    }

}
