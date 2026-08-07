package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuporteNovaMensagemRequestDTO {

    @NotBlank(message = "A mensagem não pode ser vazia")
    private String conteudo;

}
