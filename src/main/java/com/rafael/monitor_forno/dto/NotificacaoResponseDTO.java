package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacaoResponseDTO {

    private LocalTime horaEnvio;
    private String mensagem;
    private String logoBase64;
    private String nomeApp;
    private UUID fornoId;

}
