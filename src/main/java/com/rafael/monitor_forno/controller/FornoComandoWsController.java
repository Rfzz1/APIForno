package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.service.FornoComandoWsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fornos")
public class FornoComandoWsController {

    private final FornoComandoWsService fornoComandoWsService;

    public FornoComandoWsController(FornoComandoWsService fornoComandoWsService) {
        this.fornoComandoWsService = fornoComandoWsService;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{serialNumber}/silenciar-buzzer")
    public ResponseEntity<Void> silenciarBuzzer(@PathVariable String serialNumber) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        fornoComandoWsService.silenciarBuzzer(serialNumber, email);
        return ResponseEntity.ok().build();
    }
}
