package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacaoRequestDTO {

    private UUID id;

    @NotBlank (message = "Hora de envio é obrigatório")
    private LocalTime horaEnvio;

    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;

    @NotBlank(message = "Imagem é obrigatória")
    private String logoBase64;

    private UUID fornoId;

}
