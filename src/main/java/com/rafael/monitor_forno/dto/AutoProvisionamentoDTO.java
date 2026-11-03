package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoProvisionamentoDTO {

    @NotBlank
    private String serialNumber;

    @NotBlank
    private String pin;

}
