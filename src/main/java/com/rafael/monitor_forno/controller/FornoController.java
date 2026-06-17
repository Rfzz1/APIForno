package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.FornoAuthDTO;
import com.rafael.monitor_forno.dto.LoginResponseDTO;
import com.rafael.monitor_forno.dto.RegistroFornoDTO;
import com.rafael.monitor_forno.dto.RegistroFornoResponseDTO;
import com.rafael.monitor_forno.service.FornoService;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/fornos")
public class FornoController {

    private final FornoService fornoService;

    public FornoController(FornoService fornoService) {
        this.fornoService = fornoService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<RegistroFornoResponseDTO> registrar(@Valid @RequestBody RegistroFornoDTO dto, Authentication authentication) {

        return ResponseEntity.ok(fornoService.registrar(dto, authentication.getName()));
    }

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseDTO> autenticar(@Valid @RequestBody FornoAuthDTO dto) {

        return ResponseEntity.ok(fornoService.autenticar(dto));
    }

}
