package com.rafael.monitor_forno.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReversaoEmailDTO {

    @NotBlank(message="Token é obrigatório")
    private String token;

}
