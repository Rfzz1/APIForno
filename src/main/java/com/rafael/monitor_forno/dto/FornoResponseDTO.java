package com.rafael.monitor_forno.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoResponseDTO {

    private UUID id;
    private String serialNumber;
    private String secret;
    private boolean ativo;

}
