package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.*;
import com.rafael.monitor_forno.enums.suporte.Categoria;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuporteRequestDTO {

    @NotBlank(message = "Categoria e obrigatório")
    private Categoria categoria;

    @NotBlank(message = "Titulo é obrigatório")
    private String titulo;

    @NotBlank(message = "Mensagem é obrigatório")
    private String mensagem;
}
