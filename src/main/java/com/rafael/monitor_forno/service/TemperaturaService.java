package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.database.repository.TemperaturaRepository;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.mappers.TemperaturaMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TemperaturaService {

    private static final double THRESHOLD = 2.0;
    private final TemperaturaRepository temperaturaRepository;
    private final TemperaturaMapper temperaturaMapper;
    private final SessaoRepository sessaoRepository;

    public TemperaturaService(TemperaturaRepository temperaturaRepository, TemperaturaMapper temperaturaMapper, SessaoRepository sessaoRepository) {
        this.temperaturaRepository = temperaturaRepository;
        this.temperaturaMapper = temperaturaMapper;
        this.sessaoRepository = sessaoRepository;
    }

    public boolean registrarLeitura(TemperaturaRequestDTO dto) {

        Double diferenca = Math.abs(dto.getTemperaturaAtual() - dto.getTemperaturaUltima());

        if (diferenca <= THRESHOLD) {
            return false;
        }

        Sessao sessao = sessaoRepository.findById(dto.getSessaoId())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Sessao não encontrada " + dto.getSessaoId()
                        )
                );

        Temperatura temperatura = new Temperatura();
        temperatura.setTemperaturaAtual(dto.getTemperaturaAtual());
        temperatura.setTemperaturaUltima(dto.getTemperaturaUltima());
        temperatura.setRegistradoEm(LocalDateTime.now());
        temperatura.setSessao(sessao);

        temperaturaRepository.save(temperatura);

        return true;
    }

    public void deleteById(UUID id) {
        Temperatura temperatura = temperaturaRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Temperatura não encontrada: " + id
                        )
                );
        temperaturaRepository.delete(temperatura);
    }

    public List<TemperaturaDTO> findAll() {
        return temperaturaRepository.findAll()
                .stream()
                .map(temperaturaMapper::toTemperaturaDTO)
                .toList();
    }

    public TemperaturaDTO findByDate(LocalDateTime registradoEm) {
        Temperatura temperatura = temperaturaRepository.findByRegistradoEm(registradoEm)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Data não encontrada: " + registradoEm
                        )
                );
        return temperaturaMapper.toTemperaturaDTO(temperatura);
    }

}
