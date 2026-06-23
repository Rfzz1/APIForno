package com.rafael.monitor_forno.mappers;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.dto.FornoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FornoMapper {

    public FornoResponseDTO toFornoResponseDTO(Forno forno) {

        return FornoResponseDTO.builder()
                .id(forno.getId())
                .serialNumber(forno.getSerialNumber())
                .build();
    }

}
