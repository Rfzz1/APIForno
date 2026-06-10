package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Telemetria;
import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.database.repository.TelemetriaRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.DashboardDTO;
import com.rafael.monitor_forno.dto.EstatisticasDTO;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TelemetriaResponseDTO;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository, UsuarioRepository usuarioRepository, SessaoRepository sessaoRepository) {
        this.telemetriaRepository = telemetriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
    }

    public void registrar(TelemetriaRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Telemetria telemetria = telemetriaRepository
                .findFirstByUsuarioOrderByAtualizadoEmDesc(usuario)
                .orElse(new Telemetria());

        telemetria.setUsuario(usuario);

        telemetria.setTemperaturaAtual(dto.getTemperaturaAtual());
        telemetria.setTemperaturaUltima(dto.getTemperaturaUltima());
        telemetria.setEstadoForno(dto.getEstadoForno());
        telemetria.setEstadoSistema(dto.getEstadoSistema());
        telemetria.setTempoLigadoMinutos(dto.getTempoLigadoMinutos());
        telemetria.setAtualizadoEm(LocalDateTime.now());

        telemetriaRepository.save(telemetria);
    }

    public TelemetriaResponseDTO buscarAtual(String email) {

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(
                            () -> new RecursoNaoEncontradoException(
                                    "Usuario não encontrado " + email
                            )
                    );

            Telemetria telemetria = telemetriaRepository.findFirstByUsuarioOrderByAtualizadoEmDesc(usuario)
                    .orElseThrow(
                            () -> new RecursoNaoEncontradoException(
                                    "Nenhuma Telemetria encontrada " + usuario
                            )
                    );

            return toTelemetriaResponseDTO(telemetria);
    }

    public DashboardDTO buscarDashboard(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Telemetria telemetria = telemetriaRepository.findFirstByUsuarioOrderByAtualizadoEmDesc(usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Nenhuma telemetria encontrada " + usuario
                        )
                );

        return toDashboardDTO(telemetria);
    }

    public EstatisticasDTO buscarEstatisticas(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        ()-> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        List<Sessao> sessoes = sessaoRepository.findAllByUsuario(usuario);

        int quantidadeSessoes = sessoes.size();
        long tempoTotalLigado = sessoes.stream()
                .filter(s -> s.getDuracaoSegundos() != null)
                .mapToLong(Sessao::getDuracaoSegundos)
                .sum();
        Double temperaturaMaxima = sessoes.stream()
                .flatMap(s -> s.getTemperaturas().stream())
                .mapToDouble(Temperatura::getTemperaturaAtual)
                .max()
                .orElse(0);

        long alertas = sessoes.stream()
                .flatMap(s -> s.getEventos().stream())
                .filter(e -> e.getTipo() == )

        return EstatisticasDTO.builder()
                .tempoTotalLigado(tempoTotalLigado)
                .quantidadeSessoes(quantidadeSessoes)
                .temperaturaMaxima(temperaturaMaxima)
                .alertas(alertas)
                .build();
    }

    private TelemetriaResponseDTO toTelemetriaResponseDTO (Telemetria telemetria) {
        return TelemetriaResponseDTO.builder()
                .temperaturaAtual(telemetria.getTemperaturaAtual())
                .temperaturaUltima(telemetria.getTemperaturaUltima())
                .estadoForno(telemetria.getEstadoForno())
                .estadoSistema(telemetria.getEstadoSistema())
                .tempoLigadoMinutos(telemetria.getTempoLigadoMinutos())
                .atualizadoEm(telemetria.getAtualizadoEm())
                .build();
    }

    private DashboardDTO toDashboardDTO (Telemetria telemetria) {
        return DashboardDTO.builder()
                .temperaturaAtual(telemetria.getTemperaturaAtual())
                .temperaturaUltima(telemetria.getTemperaturaUltima())
                .estadoForno(telemetria.getEstadoForno())
                .estadoSistema(telemetria.getEstadoSistema())
                .tempoLigadoMinutos(telemetria.getTempoLigadoMinutos())
                .build();
    }

}