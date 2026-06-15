package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
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

    public TemporizadorService(TemporizadorRepository temporizadorRepository, UsuarioRepository usuarioRepository) {
        this.temporizadorRepository = temporizadorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void criarTemporizador(TemporizadorRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        if (dto.getHorarioFim().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "O horário deve estar no futuro"
            );
        }

        Temporizador temporizador = new Temporizador();
        temporizador.setCriadoEm(LocalDateTime.now());
        temporizador.setHorarioFim(dto.getHorarioFim());
        temporizador.setExecutado(false);
        temporizador.setUsuario(usuario);
        temporizadorRepository.save(temporizador);

    }

    public TemporizadorResponseDTO buscarProximoTemporizador(String email) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        Temporizador temporizador = temporizadorRepository.findFirstByUsuarioAndExecutadoFalseOrderByHorarioFimAsc(usuario)
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
                .findByUsuario(usuario)
                .stream()
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

        Temporizador temporizadorExistente = temporizadorRepository.findByIdAndUsuario(id, usuario)
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

    public void marcarComoExecutado(UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " +email
                        )
                );

        Temporizador temporizador = temporizadorRepository.findByIdAndUsuario(id, usuario)
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

        Temporizador temporizador = temporizadorRepository.findByIdAndUsuario(id, usuario)
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
                                "Usuário não encontrado " + email
                        )
                );

        Temporizador temporizador = temporizadorRepository.findByIdAndUsuario(id, usuario)
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
