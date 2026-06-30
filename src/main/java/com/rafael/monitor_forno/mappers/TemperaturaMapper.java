package com.rafael.monitor_forno.mappers;

import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import org.springframework.stereotype.Component;

@Component
public class TemperaturaMapper {

    public TemperaturaDTO toTemperaturaDTO(Temperatura temperatura) {
        return TemperaturaDTO.builder()
                .id(temperatura.getId())
                .registradoEm(temperatura.getRegistradoEm())
                .temperaturaAtual(temperatura.getTemperaturaAtual())
                .temperaturaUltima(temperatura.getTemperaturaUltima())
                .temperaturaExterna(temperatura.getTemperaturaExterna())
                .build();
    }

}
