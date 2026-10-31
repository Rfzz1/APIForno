package com.rafael.monitor_forno.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Notificacao;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.NotificacaoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.NotificacaoRequestDTO;
import com.rafael.monitor_forno.enums.Role;
import com.rafael.monitor_forno.enums.notificacao.StatusN;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final FornoRepository fornoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.nome-app-dev}")
    private String appDevName;

    @Value("${app.nome-app-comum}")
    private String appComumName;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, FornoRepository fornoRepository,UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.fornoRepository = fornoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void enviarNotificacao(NotificacaoRequestDTO dto, String email) throws FirebaseMessagingException {

        Forno forno = fornoRepository.findById(dto.getFornoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum forno encontrado"));

        Usuario fornoUsuario = forno.getUsuario();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum usuário encontrado"));

        if (fornoUsuario.getId().equals(usuario.getId())) {
            // Lógica para enviar notificação

            Notificacao notificacao = new Notificacao();
            notificacao.setForno(forno);
            notificacao.setMensagem(dto.getMensagem());
            notificacao.setHoraEnvio(dto.getHoraEnvio().toInstant());
            notificacao.setStatusN(StatusN.PENDENTE);

            if (Role.ADMIN == usuario.getRole()) {
                notificacao.setNomeApp(appDevName);
            } else {
                notificacao.setNomeApp(appComumName);
            }

            notificacao.setLogoUrl(dto.getLogoUrl());

            notificacaoRepository.save(notificacao);

        } else {
            throw new AcessoNegadoException("Você não possui acesso a esse forno");
        }
    }

    @Scheduled(fixedRate = 60000) // Executa a cada minuto
    public void verificarNotificacaoPendente() throws FirebaseMessagingException {

        Instant agora = Instant.now();
        List<Notificacao> notificacoesPendentes = notificacaoRepository.findNotificacaoPendente(agora);

        for (Notificacao notificacao : notificacoesPendentes) {
            Notification notification = Notification.builder()
                .setTitle(notificacao.getNomeApp())
                .setBody(notificacao.getMensagem())
                .setImage(notificacao.getLogoUrl())
                .build();

            Message mensagem = Message.builder().setNotification(notification).setTopic(notificacao.getForno().getSerialNumber()).build();
            FirebaseMessaging fmt = FirebaseMessaging.getInstance();
            fmt.send(mensagem);

            notificacao.setStatusN(StatusN.ENVIADO);
            notificacaoRepository.save(notificacao);
        }

    }

}
