package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.TemporizadorRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.TemporizadorRequestDTO;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        Temporizador temporizador = new Temporizador();
        temporizador.setCriadoEm(LocalDateTime.now());
        temporizador.setHorarioFim(dto.getHorarioFim());
        temporizador.setExecutado(false);
        temporizador.setUsuario(usuario);
        temporizadorRepository.save(temporizador);

    }

    private TemporizadorRequestDTO toTemporizadorRequestDTO(Temporizador temporizador) {
        return TemporizadorRequestDTO.builder()
                .horarioFim(temporizador.getHorarioFim())
                .build();
    }
}
