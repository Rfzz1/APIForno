package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacaoRequestDTO {

    private UUID id;

    @NotNull(message = "Hora de envio é obrigatório")
    private ZonedDateTime horaEnvio;

    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;

    @NotBlank(message = "Imagem é obrigatória")
    private String logoBase64;

    private UUID fornoId;

}
