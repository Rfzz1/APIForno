package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.estados.EstadoForno;
import com.rafael.monitor_forno.enums.estados.EstadoSistema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessaoEncerrarDTO {

    @NotNull(message = "O estado final do sistema não pode ser nulo")
    private EstadoSistema estadoSistemaFinal;

    @NotNull(message = "O estado final do forno não pode ser nulo")
    private EstadoForno estadoFornoFinal;

}
