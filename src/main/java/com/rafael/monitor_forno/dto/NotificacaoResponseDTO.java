package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacaoResponseDTO {

    private Instant horaEnvio;
    private String mensagem;
    private String logoUrl;
    private String nomeApp;
    private UUID fornoId;

}
