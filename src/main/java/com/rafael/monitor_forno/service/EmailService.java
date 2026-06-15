package com.rafael.monitor_forno.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("rafinhafiorioofc@gmail.com");
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(assunto);
        mailMessage.setText(mensagem);

        mailSender.send(mailMessage);
    }

}
