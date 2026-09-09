package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoCompartilhadoRequestDTO {

    @NotBlank(message = "SerialNumber é obrigatório")
    private String serialNumber;

    @NotBlank(message = "Email é obrigatório")
    private String email;
}
