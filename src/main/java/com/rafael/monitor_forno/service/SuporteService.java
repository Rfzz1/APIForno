package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.MensagemSuporte;
import com.rafael.monitor_forno.database.model.Suporte;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.SuporteRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.SuporteMensagemResponseDTO;
import com.rafael.monitor_forno.dto.SuporteNovaMensagemRequestDTO;
import com.rafael.monitor_forno.dto.SuporteRequestDTO;
import com.rafael.monitor_forno.dto.SuporteResponseDTO;
import com.rafael.monitor_forno.enums.suporte.Status;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.exception.SessaoEncerradaException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private Usuario buscarUsuarioLogado (String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
    }

    // ====== MÉTODOS COMPARTILHADOS - USER/ADMIN ========

    public void criarTicket (SuporteRequestDTO dto, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Suporte suporte = new Suporte();
        suporte.setCategoria(dto.getCategoria());
        suporte.setStatus(Status.ABERTO);
        suporte.setTitulo(dto.getTitulo());
        suporte.setUsuario(usuario);

        //Primeira mensagem do chat
        MensagemSuporte primeiraMensagem = new MensagemSuporte();
        primeiraMensagem.setConteudo(dto.getMensagem());
        primeiraMensagem.setDataEnvio(LocalDateTime.now());
        primeiraMensagem.setRemetente(usuario);
        primeiraMensagem.setSuporte(suporte);

        suporte.getMensagens().add(primeiraMensagem);
        suporteRepository.save(suporte);
    }

    public List<SuporteResponseDTO> findAllBySuporteUsuario (String email) {
        Usuario usuario = buscarUsuarioLogado(email);

        return suporteRepository.findAllBySuporteUsuario(usuario)
                .stream()
                .map(this::toSuporteResponseDTO)
                .toList();
    }

    public SuporteResponseDTO buscarTicket (String email, UUID id) {

        Usuario usuario = buscarUsuarioLogado(email);

        Suporte Suporte = suporteRepository.findByIdAndSuporteUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado: " + id
                        )
                );

        return toSuporteResponseDTO(Suporte);
    }

    public void adicionarMensagemChat (UUID id, SuporteNovaMensagemRequestDTO dto, String email) {

        Usuario usuario = buscarUsuarioLogado(email);

        Suporte suporte = suporteRepository.findByIdAndSuporteUsuario(id, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado: " + id
                        )
                );

        boolean isDono = suporte.getUsuario().getId().equals(usuario.getId());
        boolean isAdmin = usuario.getRole().toString().equals("ADMIN");

        if (!isDono && !isAdmin) {
            throw new AcessoNegadoException("Você não tem permissão para adicionar uma mensagem a este ticket.");
        }

        if (suporte.getStatus() == Status.RESOLVIDO) {
            throw new SessaoEncerradaException("Não é possível responder a um ticket já finalizado.");
        }

        MensagemSuporte novaMensagem = new MensagemSuporte();
        novaMensagem.setConteudo(dto.getConteudo());
        novaMensagem.setDataEnvio(LocalDateTime.now());
        novaMensagem.setRemetente(usuario);
        novaMensagem.setSuporte(suporte);

        suporte.getMensagens().add(novaMensagem);

        if (isDono) {
            suporte.setStatus(Status.ABERTO);
        } else {
            suporte.setStatus(Status.EM_ANDAMENTO);
        }

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

    public void finalizarticket (UUID id) {

        Suporte suporte = suporteRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Ticket não encontrado: " + id
                        )
                );

        suporte.setStatus(Status.RESOLVIDO);
        suporteRepository.save(suporte);
    }

    private SuporteResponseDTO toSuporteResponseDTO(Suporte suporte) {
        List<SuporteMensagemResponseDTO> mensagensDTO = suporte.getMensagens()
                .stream()
                .map(mensagem -> SuporteMensagemResponseDTO.builder()
                        .id(mensagem.getId())
                        .conteudo(mensagem.getConteudo())
                        .dataEnvio(mensagem.getDataEnvio())
                        .nomeRemetente(mensagem.getRemetente().getNome())
                        .build())
                .collect(Collectors.toList());


        return SuporteResponseDTO.builder()
                .id(suporte.getId())
                .categoria(suporte.getCategoria())
                .status(suporte.getStatus())
                .titulo(suporte.getTitulo())
                .mensagens(mensagensDTO)
                .build();
    }

}
