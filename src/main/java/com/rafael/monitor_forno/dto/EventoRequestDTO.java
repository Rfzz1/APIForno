package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRequestDTO {

    @NotNull(message = "O tipo de evento é obrigatório")
    private EventoSistema tipo;

    @NotNull(message = "O ID da sessão é obrigatório")
    private UUID sessaoId;

    @NotNull(message = "O ID do forno é obrigatório")
    private UUID fornoId;
}
