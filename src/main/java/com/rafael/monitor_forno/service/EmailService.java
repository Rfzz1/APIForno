package com.rafael.monitor_forno.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        try {
            // Cria um e-mail com suporte a formatos mais complexos (HTML, anexos, etc.)
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // O "utf-8" garante que acentos como "Bem vindo(a)" não fiquem bugados
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("fornomonitoramento@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject(assunto);

            // O 'true' no segundo parâmetro é o que ativa a leitura de HTML!
            helper.setText(mensagem, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // Trata o erro caso o e-mail falhe na montagem
            throw new RuntimeException("Falha ao enviar e-mail para " + destinatario, e);
        }
    }

}
