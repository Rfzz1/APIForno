package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.database.repository.TemperaturaRepository;
import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.exception.DataTemperaturaNaoEncontradaException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemperaturaService {

    private static final double THRESHOLD = 2.0;
    private final TemperaturaRepository temperaturaRepository;

    public TemperaturaService(TemperaturaRepository temperaturaRepository) {
        this.temperaturaRepository = temperaturaRepository;
    }

    public boolean registrarLeitura(TemperaturaRequestDTO dto) {

        Double diferenca = Math.abs(dto.getTemperaturaAtual() - dto.getTemperaturaUltima());

        if (diferenca <= THRESHOLD) {
            return false;
        }

        Temperatura temperatura = new Temperatura();
        temperatura.setTemperaturaAtual(dto.getTemperaturaAtual());
        temperatura.setTemperaturaUltima(dto.getTemperaturaUltima());
        temperatura.setRegistradoEm(LocalDateTime.now());

        temperaturaRepository.save(temperatura);

        return true;
    }

    public List<TemperaturaDTO> findAll() {
        return temperaturaRepository.findAll()
                .stream()
                .map(this::toTemperaturaDTO)
                .toList();
    }

    public TemperaturaDTO findByDate(LocalDateTime registradoEm) {
        Temperatura temperatura = temperaturaRepository.findByRegistradoEm(registradoEm)
                .orElseThrow(
                        () -> new DataTemperaturaNaoEncontradaException(
                                "Data não encontrada: " + registradoEm
                        )
                );
        return toTemperaturaDTO(temperatura);
    }

    private TemperaturaDTO toTemperaturaDTO(Temperatura temperatura) {
        return TemperaturaDTO.builder()
                .id(temperatura.getId())
                .registradoEm(temperatura.getRegistradoEm())
                .temperaturaAtual(temperatura.getTemperaturaAtual())
                .temperaturaUltima(temperatura.getTemperaturaUltima())
                .build();
    }

}
