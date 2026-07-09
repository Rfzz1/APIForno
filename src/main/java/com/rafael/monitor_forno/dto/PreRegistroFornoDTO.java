package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreRegistroFornoDTO {

    @NotBlank(message = "Serial number é obrigatório")
    private String serialNumber;

}
