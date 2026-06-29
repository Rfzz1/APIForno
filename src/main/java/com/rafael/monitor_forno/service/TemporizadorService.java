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
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TemporizadorService {

    private final TemporizadorRepository temporizadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final FornoRepository fornoRepository;

    public TemporizadorService(TemporizadorRepository temporizadorRepository, UsuarioRepository usuarioRepository, FornoRepository fornoRepository) {
        this.temporizadorRepository = temporizadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.fornoRepository = fornoRepository;
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

        if (dto.getHorarioFim().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "O horário deve estar no futuro"
            );
        }

        Temporizador temporizador = new Temporizador();
        temporizador.setCriadoEm(LocalDateTime.now());
        temporizador.setHorarioFim(dto.getHorarioFim());
        temporizador.setExecutado(false);
        temporizador.setForno(forno);
        temporizadorRepository.save(temporizador);

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

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

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

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " +email
                        )
                );

        Temporizador temporizadorExistente = temporizadorRepository.findByIdAndFornoUsuario(id, usuario)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Temporizador não pertence ao usuário logado"
                        )
                );

        if (dto.getHorarioFim().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "O horário deve estar no futuro"
            );
        }

        temporizadorExistente.setHorarioFim(dto.getHorarioFim());

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

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " +email
                        )
                );

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

    public TemporizadorResponseDTO findById(UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " +email
                        )
                );

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
                .criadoEm(temporizador.getCriadoEm())
                .horarioFim(temporizador.getHorarioFim())
                .executado(temporizador.isExecutado())
                .build();
    }
}
