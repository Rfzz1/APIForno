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
import com.rafael.monitor_forno.dto.NotificacaoRequestDTO;
import com.rafael.monitor_forno.enums.Role;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final FornoRepository fornoRepository;

    @Value("${app.nome-app-dev}")
    private String appDevName;

    @Value("${app.nome-app-comum}")
    private String appComumName;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, FornoRepository fornoRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.fornoRepository = fornoRepository;
    }

    public void enviarNotificacao(NotificacaoRequestDTO dto, String serialNumber) throws FirebaseMessagingException {

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum forno encontrado"));

        Usuario usuario = forno.getUsuario();

        Notificacao notificacao = new Notificacao();
        notificacao.setForno(forno);
        notificacao.setMensagem(dto.getMensagem());
        notificacao.setHoraEnvio(dto.getHoraEnvio());

        if (Role.ADMIN == usuario.getRole()) {
            notificacao.setNomeApp(appDevName);
        } else {
            notificacao.setNomeApp(appComumName);
        }

        notificacao.setLogoBase64(dto.getLogoBase64());

        notificacaoRepository.save(notificacao);

        Notification notification = Notification.builder()
                .setTitle(notificacao.getNomeApp())
                .setBody(notificacao.getMensagem())
                .setImage(notificacao.getLogoBase64())
                .build();

        Message mensagem = Message.builder().setNotification(notification).setTopic(serialNumber).build();

        FirebaseMessaging fmt = FirebaseMessaging.getInstance();
        fmt.send(mensagem);
    }

}
