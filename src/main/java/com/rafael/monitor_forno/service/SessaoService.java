package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.SessaoDetalhesDTO;
import com.rafael.monitor_forno.dto.SessaoResumoDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.exception.SessaoEncerradaException;
import com.rafael.monitor_forno.mappers.EventoMapper;
import com.rafael.monitor_forno.mappers.FornoMapper;
import com.rafael.monitor_forno.mappers.TemperaturaMapper;
import com.rafael.monitor_forno.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final EventoMapper eventoMapper;
    private final TemperaturaMapper temperaturaMapper;
    private final UsuarioRepository usuarioRepository;
    private final UserMapper userMapper;
    private final FornoRepository fornoRepository;
    private final FornoMapper fornoMapper;

    public SessaoService(SessaoRepository sessaoRepository, EventoMapper eventoMapper, TemperaturaMapper temperaturaMapper, UsuarioRepository usuarioRepository, UserMapper userMapper, FornoRepository fornoRepository, FornoMapper fornoMapper) {
        this.sessaoRepository = sessaoRepository;
        this.eventoMapper = eventoMapper;
        this.temperaturaMapper = temperaturaMapper;
        this.usuarioRepository = usuarioRepository;
        this.userMapper = userMapper;
        this.fornoRepository = fornoRepository;
        this.fornoMapper = fornoMapper;
    }

    public SessaoResumoDTO iniciarSessao(String serialNumber) {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + serialNumber
                        )
                );

        Sessao sessao = new Sessao();
        sessao.setForno(forno);
        sessao.setInicioSessao(LocalDateTime.now());
        Sessao sessaoSalva = sessaoRepository.save(sessao);

        return toResumoDTO(sessaoSalva);
    }

    public SessaoDetalhesDTO encerrarSessao(UUID id, String serialNumber) {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + serialNumber
                        )
                );

        Sessao sessao = sessaoRepository.findByIdAndForno(id, forno)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Sessão não pertence ao usuário logado"
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

    public void deleteById(UUID id, String serialNumber) {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + serialNumber
                        )
                );

        Sessao sessao = sessaoRepository.findByIdAndForno(id, forno)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Sessão não pertence ao forno"
                        )
                );

        sessaoRepository.delete(sessao);
    }

    public List<SessaoDetalhesDTO> findAllSessoesByUsuarioAndInicioSessaoBetween(String email, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        if (dataInicio == null || dataFim == null) {
            return sessaoRepository.findAllByFornoUsuario(usuario)
                    .stream()
                    .map(this::toDetalhesDTO)
                    .toList();
        }

        return sessaoRepository.findAllByFornoUsuarioAndInicioSessaoBetween(usuario, dataInicio, dataFim)
                .stream()
                .map(this::toDetalhesDTO)
                .toList();
    }

    public List<SessaoResumoDTO> findAll() {
        return sessaoRepository.findAll()
                .stream()
                .map(this::toResumoDTO)
                .toList();
    }

    public SessaoDetalhesDTO findById(UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Sessao sessao = sessaoRepository
                .findByIdAndFornoUsuario(id, usuario)
                .orElseThrow(() ->
                        new AcessoNegadoException(
                                "Sessão não pertence ao usuário logado"
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
                .estadoSistema(sessao.getEstadoSistema())
                .build();
    }

    private SessaoDetalhesDTO toDetalhesDTO(Sessao sessao) {
        return SessaoDetalhesDTO.builder()
                .id(sessao.getId())
                .inicioSessao(sessao.getInicioSessao())
                .fimSessao(sessao.getFimSessao())
                .estadoFornoFinal(sessao.getEstadoFornoFinal())
                .duracaoSegundos(sessao.getDuracaoSegundos())
                .estadoSistema(sessao.getEstadoSistema())
                .eventos(
                        sessao.getEventos() == null
                        ? List.of()
                        : sessao.getEventos().stream().map(eventoMapper::toEventoDTO).toList())
                .temperaturas(
                        sessao.getTemperaturas() == null
                        ? List.of()
                        : sessao.getTemperaturas().stream().map(temperaturaMapper::toTemperaturaDTO).toList())
                .forno(fornoMapper.toFornoResponseDTO(sessao.getForno()))
                .build();
    }
}
