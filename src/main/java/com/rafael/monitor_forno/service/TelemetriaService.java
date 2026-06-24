package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.*;
import com.rafael.monitor_forno.database.repository.*;
import com.rafael.monitor_forno.dto.DashboardDTO;
import com.rafael.monitor_forno.dto.EstatisticasDTO;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TelemetriaResponseDTO;
import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final EventoRepository eventoRepository;
    private final TemporizadorRepository temporizadorRepository;
    private final TemperaturaRepository temperaturaRepository;
    private final FornoRepository fornoRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository, UsuarioRepository usuarioRepository, SessaoRepository sessaoRepository, EventoRepository eventoRepository,TemporizadorRepository temporizadorRepository, TemperaturaRepository temperaturaRepository, FornoRepository fornoRepository) {
        this.telemetriaRepository = telemetriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
        this.eventoRepository = eventoRepository;
        this.temporizadorRepository = temporizadorRepository;
        this.temperaturaRepository = temperaturaRepository;
        this.fornoRepository = fornoRepository;
    }

    public void registrar(TelemetriaRequestDTO dto, String serialNumber) {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " +serialNumber
                        )
                );

        Telemetria telemetria = telemetriaRepository
                .findFirstByFornoOrderByAtualizadoEmDesc(forno)
                .orElse(new Telemetria());

        telemetria.setForno(forno);

        telemetria.setTemperaturaAtual(dto.getTemperaturaAtual());
        telemetria.setTemperaturaUltima(dto.getTemperaturaUltima());
        telemetria.setEstadoForno(dto.getEstadoForno());
        telemetria.setEstadoSistema(dto.getEstadoSistema());
        telemetria.setTempoLigadoMinutos(dto.getTempoLigadoMinutos());
        telemetria.setAtualizadoEm(LocalDateTime.now());

        telemetriaRepository.save(telemetria);
    }

    public TelemetriaResponseDTO buscarAtual(UUID fornoId, String email) {

        Forno forno = validarFornoDoUsuario(fornoId, email);

        Telemetria telemetria = telemetriaRepository.findFirstByFornoOrderByAtualizadoEmDesc(forno)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Nenhuma Telemetria encontrada " + forno
                        )
                );

        return toTelemetriaResponseDTO(telemetria);
    }

    public DashboardDTO buscarDashboard(UUID fornoId, String email) {
        Forno forno = validarFornoDoUsuario(fornoId, email);

        Telemetria telemetria = telemetriaRepository.findFirstByFornoOrderByAtualizadoEmDesc(forno)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Nenhuma telemetria encontrada " + forno
                        )
                );


        int quantidadeSessoes =
                sessaoRepository.countByFornoUsuario(forno.getUsuario());

        Double temperaturaMaxima = Optional.ofNullable(temperaturaRepository.findTemperaturaMaximaByFornoUsuario(forno.getUsuario())).orElse(0.0);

        Evento ultimoEvento = eventoRepository.findFirstByFornoUsuarioOrderByCriadoEmDesc(forno.getUsuario())
                .orElse(null);

        Temporizador proximoTemporizador = temporizadorRepository.findFirstByFornoAndExecutadoFalseOrderByHorarioFimAsc(forno)
                .orElse(null);

        return DashboardDTO.builder()
                .fornoId(forno.getId())
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

        List<Sessao> sessoes = sessaoRepository.findAllByFornoUsuario(usuario);

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

    private Forno validarFornoDoUsuario(UUID fornoId, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado " + email));

        Forno forno = fornoRepository.findById(fornoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Forno não encontrado"));

        if (!forno.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Este forno não pertence ao usuário logado.");
        }
        return forno;
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