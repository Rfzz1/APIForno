package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NovaSenhaDTO(

        @NotBlank(message = "Token é obrigatório")
        String token,

        @NotBlank(message = "Senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "A senha deve possuir no mínimo 8 caracteres, uma letra maiúscula, uma minúscula e um número"
        )
        String novaSenha
) {}
