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
public class SessaoAtualizarDTO {

    @NotNull(message = "O estado do sistema não pode ser nulo")
    private EstadoSistema estadoSistemaAtual;

    @NotNull(message = "O estado do forno não pode ser nulo")
    private EstadoForno estadoFornoAtual;
}
