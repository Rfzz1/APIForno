package com.rafael.monitor_forno.dto;

public record ConfirmarTrocaEmailDTO (
        @NotBlank(message = "O código é obrigatório")
        String codigo
){}
