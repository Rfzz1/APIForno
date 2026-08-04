package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.*;

public record ConfirmarTrocaEmailDTO (
        @NotBlank(message = "O código é obrigatório")
        String codigo
){}
