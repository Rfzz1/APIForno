package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.database.model.Usuario;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshTokenResponseDTO {

    private String refreshToken;
    private LocalDateTime expiration;
    private String novoToken;

}
