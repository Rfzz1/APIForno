package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.dto.RegistroFornoDTO;
import com.rafael.monitor_forno.dto.RegistroFornoResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FornoService {

    private final FornoRepository fornoRepository;

    public FornoService(FornoRepository fornoRepository) {
        this.fornoRepository = fornoRepository;
    }

    public RegistroFornoResponseDTO registrar(RegistroFornoDTO dto) {

        Optional<Forno> fornoExistente = fornoRepository.findBySerialNumber(dto.getSerialNumber());

        if (fornoExistente.isPresent()) {
            return toRegistroFornoResponseDTO(fornoExistente.get());
        }

        Forno forno = new Forno();
        forno.setSerialNumber(dto.getSerialNumber());

        forno.setDeviceSecret(
                UUID.randomUUID().toString()
        );

        forno.setAtivo(true);

        fornoRepository.save(forno);

        return toRegistroFornoResponseDTO(forno);
    }

    private RegistroFornoResponseDTO toRegistroFornoResponseDTO(Forno forno) {
        return RegistroFornoResponseDTO.builder()
                .serialNumber(forno.getSerialNumber())
                .secret(forno.getDeviceSecret())
                .build();
    }

}
