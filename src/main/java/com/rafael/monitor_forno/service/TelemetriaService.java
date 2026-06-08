package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Telemetria;
import com.rafael.monitor_forno.database.repository.TelemetriaRepository;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

}
