package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.*;
import com.rafael.monitor_forno.database.repository.*;
import com.rafael.monitor_forno.dto.DashboardDTO;
import com.rafael.monitor_forno.dto.EstatisticasDTO;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TelemetriaResponseDTO;
import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final EventoRepository eventoRepository;
    private final TemporizadorRepository temporizadorRepository;
    private final TemperaturaRepository temperaturaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository, UsuarioRepository usuarioRepository, SessaoRepository sessaoRepository, EventoRepository eventoRepository,TemporizadorRepository temporizadorRepository, TemperaturaRepository temperaturaRepository) {
        this.telemetriaRepository = telemetriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
        this.eventoRepository = eventoRepository;
        this.temporizadorRepository = temporizadorRepository;
        this.temperaturaRepository = temperaturaRepository;
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


        int quantidadeSessoes =
                sessaoRepository.countByUsuario(usuario);

        Double temperaturaMaxima = temperaturaRepository.findTemperaturaMaximaByUsuario(usuario);

        Evento ultimoEvento = eventoRepository.findFirstByUsuarioOrderByCriadoEmDesc(usuario)
                .orElse(null);

        Temporizador proximoTemporizador = temporizadorRepository.findFirstByUsuarioAndExecutadoFalseOrderByHorarioFimAsc(usuario)
                .orElse(null);

        return DashboardDTO.builder()
                .temperaturaAtual(telemetria.getTemperaturaAtual())
                .temperaturaUltima(telemetria.getTemperaturaUltima())
                .estadoForno(telemetria.getEstadoForno())
                .estadoSistema(telemetria.getEstadoSistema())
                .tempoLigadoMinutos(telemetria.getTempoLigadoMinutos())
                .quantidadeSessoes(quantidadeSessoes)
                .temperaturaMaxima(temperaturaMaxima)
                .ultimoEvento(
                        ultimoEvento != null
                        ? ultimoEvento.getTipo()
                        : null
                )
                .proximoTemporizador(
                        proximoTemporizador != null
                        ? proximoTemporizador.getHorarioFim()
                        : null
                )
                .atualizadoEm(telemetria.getAtualizadoEm())
                .build();
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
                .filter(e -> e.getTipo() == EventoSistema.ALERTA_ENTRADA || e.getTipo() == EventoSistema.ALERTA_SAIDA)
                .count();

        long criticos = sessoes.stream()
                .flatMap(s -> s.getEventos().stream())
                .filter(e -> e.getTipo() == EventoSistema.CRITICO_ENTRADA || e.getTipo() == EventoSistema.CRITICO_SAIDA)
                .count();

        long erros = sessoes.stream()
                .flatMap(s -> s.getEventos().stream())
                .filter(e -> e.getTipo() == EventoSistema.ERRO_SENSOR_ENTRADA || e.getTipo() == EventoSistema.ERRO_SENSOR_SAIDA)
                .count();

        return EstatisticasDTO.builder()
                .tempoTotalLigado(tempoTotalLigado)
                .quantidadeSessoes(quantidadeSessoes)
                .temperaturaMaxima(temperaturaMaxima)
                .alertas(alertas)
                .criticos(criticos)
                .errosSensor(erros)
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

}