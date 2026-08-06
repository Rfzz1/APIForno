package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Suporte;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.SuporteRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.SuporteRequestDTO;
import com.rafael.monitor_forno.dto.SuporteResponseDTO;
import com.rafael.monitor_forno.enums.suporte.Status;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SuporteService {

    private final SuporteRepository suporteRepository;
    private final UsuarioRepository usuarioRepository;

    public SuporteService(SuporteRepository suporteRepository, UsuarioRepository usuarioRepository) {
        this.suporteRepository = suporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void criarTicket (SuporteRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );

        Suporte suporte = new Suporte();
        suporte.setCategoria(dto.getCategoria());
        suporte.setStatus(Status.ABERTO);
        suporte.setTitulo(dto.getTitulo());
        suporte.setMensagem(dto.getMensagem());
        suporte.setUsuario(usuario);

        suporteRepository.save(suporte);
    }

    public List<SuporteResponseDTO> findAllBySuporteUsuario (String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
        return suporteRepository.findAllBySuporteUsuario(usuario)
                .stream()
                .map(this::toSuporteResponseDTO)
                .toList();
    }

    public SuporteResponseDTO buscarTicket (String email, UUID id) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );

        Suporte Suporte = suporteRepository.findByIdAndSuporteUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado: " + id
                        )
                );

        return toSuporteResponseDTO(Suporte);
    }

    public void atualizarTicket (UUID id, SuporteRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );

        Suporte suporte = suporteRepository.findByIdAndSuporteUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado: " + id
                        )
                );

        suporte.setTitulo(dto.getTitulo());
        suporte.setMensagem(dto.getMensagem());
        suporte.setCategoria(dto.getCategoria());

        suporteRepository.save(suporte);
    }

    public void deletarTicket (UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );

        Suporte suporte = suporteRepository.findByIdAndSuporteUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado para esse usuário: " + email
                        )
                );

        suporteRepository.delete(suporte);
    }

    // ========= ADMIN ========

    public List<SuporteResponseDTO> listarTickets () {

        return suporteRepository.findAll()
                .stream()
                .map(this::toSuporteResponseDTO)
                .collect(Collectors.toList());
    }

    public void atualizarStatusTicket (UUID id, SuporteRequestDTO dto) {

        Suporte suporte = suporteRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado: " + id
                        )
                );

        suporte.setStatus(dto.getStatus());
        suporteRepository.save(suporte);
    }

    private SuporteResponseDTO toSuporteResponseDTO(Suporte suporte) {
        return SuporteResponseDTO.builder()
                .id(suporte.getId())
                .categoria(suporte.getCategoria())
                .status(suporte.getStatus())
                .titulo(suporte.getTitulo())
                .mensagem(suporte.getMensagem())
                .build();
    }

}
