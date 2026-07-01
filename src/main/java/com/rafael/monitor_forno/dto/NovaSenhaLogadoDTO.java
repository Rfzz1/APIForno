package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NovaSenhaLogadoDTO(

        @NotBlank(message = "Nova senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "A senha deve possuir no mínimo 8 caracteres, uma letra maiúscula, uma minúscula e um número"
        )
        String senhaAtualizada,

        @NotBlank(message = "Senha atual é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "A senha deve possuir no mínimo 8 caracteres, uma letra maiúscula, uma minúscula e um número"
        )
        String senhaAtual
) {}
