package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VincularFornoDTO {

    @NotBlank(message = "Serial number é obrigatório")
    private String serialNumber;

    @NotBlank(message = "PIN de segurança é obrigatório")
    private String pinSeguranca;

    @NotBlank(message = "Nome do forno é obrigatório")
    private String nome;

}
