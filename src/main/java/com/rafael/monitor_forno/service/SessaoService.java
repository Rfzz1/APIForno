package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.exception.SessaoEncerradaException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;

    public SessaoService(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public SessaoResumoDTO iniciarSessao() {

        Sessao sessao = new Sessao();

        sessao.setInicioSessao(LocalDateTime.now());

        Sessao sessaoSalva = sessaoRepository.save(sessao);

        return toResumoDTO(sessaoSalva);
    }

    public SessaoDetalhesDTO encerrarSessao(UUID id) {

        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Sessão não encontrada: " + id
                        )
                );

        if (sessao.getFimSessao() != null) {
            throw new SessaoEncerradaException(
                    "Sessão já encerrada."
            );
        }

        sessao.setFimSessao(LocalDateTime.now());

        long duracaoSegundos = ChronoUnit.SECONDS.between(
                sessao.getInicioSessao(),
                sessao.getFimSessao()
        );

        sessao.setDuracaoSegundos(duracaoSegundos);

        Sessao sessaoSalva = sessaoRepository.save(sessao);

        return toDetalhesDTO(sessaoSalva);
    }

    public void deleteById(UUID id) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Sessão não encontrada: " + id
                        )
                );

        sessaoRepository.delete(sessao);
    }

    public List<SessaoResumoDTO> findAll() {
        return sessaoRepository.findAll()
                .stream()
                .map(this::toResumoDTO)
                .toList();
    }

    public SessaoDetalhesDTO findById(UUID id) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Sessão não encontrada: " + id
                        )
                );

        return toDetalhesDTO(sessao);
    }

    private SessaoResumoDTO toResumoDTO(Sessao sessao) {
        return SessaoResumoDTO.builder()
                .id(sessao.getId())
                .inicioSessao(sessao.getInicioSessao())
                .fimSessao(sessao.getFimSessao())
                .estadoFornoFinal(sessao.getEstadoFornoFinal())
                .build();
    }

    private SessaoDetalhesDTO toDetalhesDTO(Sessao sessao) {
        return SessaoDetalhesDTO.builder()
                .id(sessao.getId())
                .inicioSessao(sessao.getInicioSessao())
                .fimSessao(sessao.getFimSessao())
                .estadoFornoFinal(sessao.getEstadoFornoFinal())
                .duracaoSegundos(sessao.getDuracaoSegundos())
                .build();
    }
}
