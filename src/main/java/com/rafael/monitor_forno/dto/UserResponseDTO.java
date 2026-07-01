package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID id;
    private String nome;
    private String email;
    private LocalDate nascimento;
    private String fotoBase64;
}
