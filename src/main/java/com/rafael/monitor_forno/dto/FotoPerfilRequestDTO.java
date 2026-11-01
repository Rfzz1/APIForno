package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FotoPerfilRequestDTO {

    private String fotoBase64;

    @NotNull(message = "O ID do usuário não pode ser nulo")
    private UUID usuarioId;
}
