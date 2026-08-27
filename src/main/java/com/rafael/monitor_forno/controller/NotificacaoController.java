package com.rafael.monitor_forno.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.rafael.monitor_forno.dto.NotificacaoRequestDTO;
import com.rafael.monitor_forno.service.NotificacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<Void> enviarNotificacao (@Valid @RequestBody NotificacaoRequestDTO dto) throws FirebaseMessagingException {

        String serialNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        notificacaoService.enviarNotificacao(dto, serialNumber);
        return ResponseEntity.ok().build();

    }

}
