package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoCompartilhadoRequestDTO {

    @NotBlank(message = "ID do forno é obrigatório")
    private UUID fornoId;

    @NotBlank(message = "Email é obrigatório")
    private String email;
}
