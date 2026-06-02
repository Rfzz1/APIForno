package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.service.SessaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SessaoResumoDTO> criarSessao() {

        SessaoResumoDTO sessao = sessaoService.iniciarSessao();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessao);
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<SessaoDetalhesDTO> encerrarSessao(@PathVariable UUID id){

        SessaoDetalhesDTO sessao = sessaoService.encerrarSessao(id);

        return ResponseEntity.ok(sessao);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSessao(@PathVariable UUID id) {
        sessaoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SessaoResumoDTO>> pegarTodasSessoes() {

        return ResponseEntity.ok(
                sessaoService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessaoDetalhesDTO> pegarSessaoPorId(@PathVariable UUID id) {

        return ResponseEntity.ok(
                sessaoService.findById(id)
        );

    }

}
