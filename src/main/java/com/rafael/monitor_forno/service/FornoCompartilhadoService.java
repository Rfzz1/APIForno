package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.FornoCompartilhado;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoCompartilhadoRepository;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.FornoCompartilhadoResponseDTO;
import com.rafael.monitor_forno.enums.compartilhamento.StatusC;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.CredencialJaCadastradaException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FornoCompartilhadoService {

    private final FornoCompartilhadoRepository fornoCompartilhadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FornoRepository fornoRepository;

    public FornoCompartilhadoService(FornoCompartilhadoRepository fornoCompartilhadoRepository, UsuarioRepository usuarioRepository, FornoRepository fornoRepository) {
        this.fornoCompartilhadoRepository = fornoCompartilhadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fornoRepository = fornoRepository;
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
    }

    private Forno buscarForno(UUID fornoId) {
        return fornoRepository.findById(fornoId)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado: " + fornoId
                        )
                );
    }

    private FornoCompartilhado buscarFornoCompartilhado(String email, UUID fornoId) {

        Usuario usuario = buscarUsuario(email);
        Forno forno = buscarForno(fornoId);

        return fornoCompartilhadoRepository.findByFornoAndUsuario(forno, usuario)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Convite não encontrado para o usuário e forno especificados"
                        )
                );

    }

    public void enviarConvite(String emailUsuario, UUID fornoId, String usuarioLogado) {

        Usuario usuarioExistente = buscarUsuario(emailUsuario);
        Forno forno = buscarForno(fornoId);

        if (fornoCompartilhadoRepository.findByFornoAndUsuario(forno, usuarioExistente).isPresent()) {
            throw new CredencialJaCadastradaException(
                    "Convite já enviado"
            );
        }

        if (!forno.getUsuario().getEmail().equals(usuarioLogado)) {
            throw new AcessoNegadoException(
                    "Você não tem permissão para enviar convites para este forno"
            );
        }


        FornoCompartilhado fornoCompartilhado = new FornoCompartilhado();
        fornoCompartilhado.setUsuario(usuarioExistente);
        fornoCompartilhado.setForno(forno);
        fornoCompartilhado.setDataConvite(Instant.now());
        fornoCompartilhado.setStatus(StatusC.PENDENTE);
        fornoCompartilhadoRepository.save(fornoCompartilhado);

    }

    public void excluirConvite(String emailUsuarioConvidado, String emailUsuarioLogado, UUID fornoId) {

        Forno forno = buscarForno(fornoId);

        if (!forno.getUsuario().getEmail().equals(emailUsuarioLogado)) {
            throw new AcessoNegadoException("Você não tem permissão para excluir convites deste forno");
        }

        FornoCompartilhado fornoCompartilhado = buscarFornoCompartilhado(emailUsuarioConvidado, fornoId);

        fornoCompartilhadoRepository.delete(fornoCompartilhado);
    }

    public FornoCompartilhadoResponseDTO aceitarConvite(String emailUsuario, UUID fornoId) {

        FornoCompartilhado fornoCompartilhado = buscarFornoCompartilhado(emailUsuario, fornoId);

        if (!fornoCompartilhado.getStatus().equals(StatusC.PENDENTE)) {
            throw new AcessoNegadoException("Convite não está pendente e não pode ser aceito");
        }

        fornoCompartilhado.setStatus(StatusC.ACEITO);
        fornoCompartilhadoRepository.save(fornoCompartilhado);

        return toFornoCompartilhadoDTO(fornoCompartilhado);
    }

    public FornoCompartilhadoResponseDTO recusarConvite(String emailUsuarioLogado, UUID fornoId) {

        FornoCompartilhado fornoCompartilhado = buscarFornoCompartilhado(emailUsuarioLogado, fornoId);

        if (!fornoCompartilhado.getStatus().equals(StatusC.PENDENTE)) {
            throw new AcessoNegadoException("Convite não está pendente e não pode ser recusado");
        }

        fornoCompartilhado.setStatus(StatusC.REJEITADO);
        fornoCompartilhadoRepository.save(fornoCompartilhado);

        return toFornoCompartilhadoDTO(fornoCompartilhado);
    }

    public List<FornoCompartilhadoResponseDTO> findAllByUsuarioAndStatus(String email, StatusC status) {
        Usuario usuarioExistente = buscarUsuario(email);

        return fornoCompartilhadoRepository.findAllByUsuarioAndStatus(usuarioExistente, status).stream()
                .map(this::toFornoCompartilhadoDTO)
                .collect(Collectors.toList());
    }

    public FornoCompartilhadoResponseDTO toFornoCompartilhadoDTO(FornoCompartilhado fornoCompartilhado) {
        return FornoCompartilhadoResponseDTO.builder()
                .serialNumber(fornoCompartilhado.getForno().getSerialNumber())
                .email(fornoCompartilhado.getUsuario().getEmail())
                .status(fornoCompartilhado.getStatus())
                .dataConvite(fornoCompartilhado.getDataConvite())
                .build();
    }


}
