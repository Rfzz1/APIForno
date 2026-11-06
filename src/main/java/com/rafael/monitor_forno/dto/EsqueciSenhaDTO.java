package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EsqueciSenhaDTO(
        @NotBlank(message = "Email é obrigatório")
         @Email(message = "Email inválido")
        String email) {
}
