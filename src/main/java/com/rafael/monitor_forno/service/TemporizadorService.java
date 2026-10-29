package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.TemporizadorRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.TemporizadorRequestDTO;
import com.rafael.monitor_forno.dto.TemporizadorResponseDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cglib.core.Local;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@EnableScheduling
@Service
public class TemporizadorService {

    private final TemporizadorRepository temporizadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final FornoRepository fornoRepository;
    private final FornoComandoWsService fornoComandoWsService;
    private final TaskScheduler  taskScheduler;

    public TemporizadorService(TemporizadorRepository temporizadorRepository, UsuarioRepository usuarioRepository, FornoRepository fornoRepository,FornoComandoWsService fornoComandoWsService, TaskScheduler taskScheduler) {
        this.temporizadorRepository = temporizadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.fornoRepository = fornoRepository;
        this.fornoComandoWsService = fornoComandoWsService;
        this.taskScheduler = taskScheduler;
    }

    private Usuario buscarUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
    }

    public void criarTemporizador(TemporizadorRequestDTO dto, UUID fornoId, String email) {

        Forno forno = fornoRepository.findById(fornoId)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado"
                        )
                );

        if (!forno.getUsuario().getEmail().equals(email)) {
            throw new AcessoNegadoException(
                    "Temporizador não pertence ao usuário logado"
            );
        }

        if (dto.getHorarioFim() == null || dto.getHorarioInicio() == null) {
            throw new CredenciaisInvalidasException(
                    "Insira valores válidos"
            );
        }

        if (dto.getHorarioFim().toInstant().isBefore(Instant.now()) || dto.getHorarioInicio().toInstant().isBefore(Instant.now().minus(1, ChronoUnit.MINUTES)) || dto.getHorarioFim().toInstant().isBefore(dto.getHorarioInicio().toInstant())) {
            throw new IllegalArgumentException(
                    "O horário deve estar no futuro"
            );
        }

        Temporizador temporizador = new Temporizador();
        temporizador.setHorarioInicio(dto.getHorarioInicio().toInstant());
        temporizador.setHorarioFim(dto.getHorarioFim().toInstant());
        temporizador.setExecutado(false);
        temporizador.setDuracaoSegundos(ChronoUnit.SECONDS.between(dto.getHorarioInicio().toInstant(), dto.getHorarioFim().toInstant()));
        temporizador.setForno(forno);
        temporizadorRepository.save(temporizador);

        Instant horarioFim = dto.getHorarioFim().toInstant();

        taskScheduler.schedule(
                () -> {
                    try {
                        temporizador.setExecutado(true);
                        temporizadorRepository.save(temporizador);
                        fornoComandoWsService.dispararBuzzer(forno.getSerialNumber(), temporizador);
                    } catch(RecursoNaoEncontradoException ex) {
                        System.out.println(ex.getMessage());
                    }
                },
                horarioFim
        );

    }

    @EventListener(ApplicationReadyEvent.class)
    public void buscarTemporizadoresNaoExecutados() {

        System.out.println("Servidor reiniciado. Buscando temporizadores não executados");

        List<Temporizador> temporizadores = temporizadorRepository.findAllByExecutadoFalse();

        for (Temporizador temporizador : temporizadores) {

            Instant horarioFim = temporizador.getHorarioFim();
            Instant agora = Instant.now();

            if (agora.isBefore(temporizador.getHorarioFim())) {
                taskScheduler.schedule(
                        () -> {
                            try {
                                temporizador.setExecutado(true);
                                temporizadorRepository.save(temporizador);
                                fornoComandoWsService.dispararBuzzer(temporizador.getForno().getSerialNumber(), temporizador);
                            } catch (RecursoNaoEncontradoException ex) {
                                System.out.println(ex.getMessage());
                            }
                        },
                        horarioFim
                );
            } else {
                temporizador.setExecutado(true);
                temporizadorRepository.save(temporizador);
            }
        }
    }

    public TemporizadorResponseDTO buscarProximoTemporizador(String serialNumber) {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado"
                        )
                );

        Temporizador temporizador = temporizadorRepository.findFirstByFornoAndExecutadoFalseOrderByHorarioFimAsc(forno)
                .orElseThrow(   
                        () -> new RecursoNaoEncontradoException(
                                "Nenhum temporizador encontrado"
                        )
                );

        return toResponseDTO(temporizador);
    }

    public List<TemporizadorResponseDTO> buscarTemporizadoresUsuario(String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        return temporizadorRepository
                .findByFornoUsuario(usuario)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<TemporizadorResponseDTO> buscarTemporizadoresFornoUsuario(UUID fornoId, String email) {

        List<Temporizador> temporizadores = temporizadorRepository.findAllByFornoIdAndFornoUsuarioEmail(fornoId, email);

        return temporizadores.stream()
                .map(this::toResponseDTO)
                .toList();

    }

    public TemporizadorResponseDTO atualizarTemporizador(TemporizadorRequestDTO dto, UUID id, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Temporizador temporizadorExistente = temporizadorRepository.findByIdAndFornoUsuario(id, usuario)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Temporizador não pertence ao usuário logado"
                        )
                );

        if (dto.getHorarioFim().toInstant().isBefore(Instant.now())) {
            throw new IllegalArgumentException(
                    "O horário deve estar no futuro"
            );
        }

        temporizadorExistente.setHorarioFim(dto.getHorarioFim().toInstant());

        return toResponseDTO(temporizadorRepository.save(temporizadorExistente));
    }

    public void marcarComoExecutado(UUID id, String serialNumber) {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado"
                        )
                );

        Temporizador temporizador = temporizadorRepository.findByIdAndForno(id, forno)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Temporizador não pertence ao usuário logado"
                        )
                );
        temporizador.setExecutado(true);
        temporizadorRepository.save(temporizador);
    }

    public void deleteById(UUID id, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Temporizador temporizador = temporizadorRepository.findByIdAndFornoUsuario(id, usuario)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Temporizador não pertence ao usuário logado "
                        )
                );

        temporizadorRepository.delete(temporizador);
    }

    public List<TemporizadorResponseDTO> findAll() {
        return temporizadorRepository.findAll()
                .stream()
                .map(this:: toResponseDTO)
                .toList();
    }

    public List<TemporizadorResponseDTO> findAllByFornoUsuario(String serialNumber, String email) {
        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado"
                        )
                );
        Usuario usuario = forno.getUsuario();

        return temporizadorRepository.findAllByFornoAndForno_Usuario(forno, usuario)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public TemporizadorResponseDTO findById(UUID id, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Temporizador temporizador = temporizadorRepository.findByIdAndFornoUsuario(id, usuario)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Temporizador não pertence ao usuário logado"
                        )
                );
        return toResponseDTO(temporizador);
    }

    private TemporizadorResponseDTO toResponseDTO(Temporizador temporizador) {
        return TemporizadorResponseDTO.builder()
                .id(temporizador.getId())
                .horarioInicio(temporizador.getHorarioInicio())
                .horarioFim(temporizador.getHorarioFim())
                .executado(temporizador.isExecutado())
                .duracaoSegundos(temporizador.getDuracaoSegundos())
                .build();
    }
}
