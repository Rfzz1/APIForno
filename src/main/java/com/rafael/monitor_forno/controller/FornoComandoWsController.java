package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.service.FornoComandoWsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fornos")
public class FornoComandoWsController {

    private final FornoComandoWsService fornoComandoWsService;

    public FornoComandoWsController(FornoComandoWsService fornoComandoWsService) {
        this.fornoComandoWsService = fornoComandoWsService;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{serialNumber}/silenciar-buzzer")
    public ResponseEntity<Void> silenciarBuzzer(@PathVariable String serialNumber, Authentication authentication) {

        fornoComandoWsService.silenciarBuzzer(serialNumber, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
