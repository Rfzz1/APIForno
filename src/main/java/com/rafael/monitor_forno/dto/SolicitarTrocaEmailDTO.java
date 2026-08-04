package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.*;

public record SolicitarTrocaEmailDTO (
        @NotBlank(message = "A senha atual é obrigatória")
      String senhaAtual,

      @NotBlank(message = "O novo e-mail é obrigatório")
      @Email(message = "E-mail em formato inválido")
      String novoEmail
) {}
