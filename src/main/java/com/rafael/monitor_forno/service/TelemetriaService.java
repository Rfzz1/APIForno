package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Telemetria;
import com.rafael.monitor_forno.database.repository.TelemetriaRepository;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

    public void registrar(TelemetriaRequestDTO dto) {
        Telemetria telemetria = new Telemetria();

        telemetria.setTemperaturaAtual(dto.getTemperaturaAtual());
        telemetria.setTemperaturaUltima(dto.getTemperaturaUltima());
        telemetria.setEstadoForno(dto.getEstadoForno());
        telemetria.setEstadoSistema(dto.getEstadoSistema());
        telemetria.setTempoLigadoMinutos(dto.getTempoLigadoMinutos());
        telemetria.setCriadoEm(LocalDateTime.now());

        telemetriaRepository.save(telemetria);
    }

}