package com.rafael.monitor_forno.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistroFornoResponseDTO {

    private UUID id;
    private String serialNumber;
    private String secret;
    private boolean ativo;

}
