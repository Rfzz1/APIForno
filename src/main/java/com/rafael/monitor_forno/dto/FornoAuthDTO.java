package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoAuthDTO {

    @NotBlank(message = "Serial Number é obrigatório")
    private String serialNumber;

    @NotBlank(message = "Device Secret é obrigatório")
    private String secret;

}
