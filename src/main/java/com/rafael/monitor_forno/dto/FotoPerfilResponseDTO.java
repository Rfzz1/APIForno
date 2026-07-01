package com.rafael.monitor_forno.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FotoPerfilResponseDTO {

    private UUID id;
    private String fotoBase64;

}
