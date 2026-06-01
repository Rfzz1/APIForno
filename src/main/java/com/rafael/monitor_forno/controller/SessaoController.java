package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.database.model.Sessao;
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
    public ResponseEntity<Sessao> criarSessao(){

        Sessao sessao = sessaoService.iniciarSessao();
        return new ResponseEntity<>(sessao, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Sessao> encerrarSessao(@PathVariable UUID id){

        Sessao sessao = sessaoService.encerrarSessao(id);

        return ResponseEntity.ok(sessao);

    }

    @GetMapping
    public ResponseEntity<List<Sessao>> pegarTodasSessoes() {

        return ResponseEntity.ok(
                sessaoService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sessao> pegarSessaoPorId(@PathVariable UUID id) {

        Sessao sessao = sessaoService.findById(id);

        return ResponseEntity.ok(sessao);

    }

}
