package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuporteMensagemResponseDTO {

    private UUID id;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private String nomeRemetente;

}
