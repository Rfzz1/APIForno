package com.rafael.monitor_forno.mappers;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.dto.RegistroFornoResponseDTO;
import com.rafael.monitor_forno.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FornoMapper {

    public RegistroFornoResponseDTO toFornoResponseDTO(Forno forno) {

        return RegistroFornoResponseDTO.builder()
                .id(forno.getId())
                .serialNumber(forno.getSerialNumber())
                .build();
    }

}
