package com.rafael.monitor_forno.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoAtualizarDTO {

    @NotBlank(message = "O nome do forno não pode ser vazio ou nulo")
    private String nome;

}
