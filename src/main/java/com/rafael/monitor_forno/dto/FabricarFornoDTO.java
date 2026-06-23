package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FabricarFornoDTO {

    @NotBlank(message = "Serial number é obrigatório")
    private String serialNumber;

    @NotBlank(message = "Nome base do modelo é obrigatório")
    private String nome;

    @NotBlank(message = "PIN de segurança (ex: mín 20 dígitos) é obrigatório")
    private String pinSeguranca;

}
