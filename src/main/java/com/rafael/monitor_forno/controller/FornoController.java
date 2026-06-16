package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.dto.RegistroFornoDTO;
import com.rafael.monitor_forno.dto.RegistroFornoResponseDTO;
import com.rafael.monitor_forno.service.FornoService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RegistroFornoResponseDTO> registrar(@RequestBody RegistroFornoDTO dto) {

        return ResponseEntity.ok(fornoService.registrar(dto));
    }


}
