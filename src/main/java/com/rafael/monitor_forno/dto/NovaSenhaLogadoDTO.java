package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.*;

public record NovaSenhaLogadoDTO(

        @NotBlank(message = "Nova senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Senha deve ter no mínimo 8 caracteres, incluindo uma letra maiúscula, uma letra minúscula, um número e um caractere especial")
        String senhaAtualizada,

        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual
) {}
