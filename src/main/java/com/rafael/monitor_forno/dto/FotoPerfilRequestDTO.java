package com.rafael.monitor_forno.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FotoPerfilRequestDTO {

    private String fotoBase64;
    private UUID usuarioId;
}
