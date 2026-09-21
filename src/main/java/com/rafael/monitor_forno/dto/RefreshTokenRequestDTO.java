package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshTokenRequestDTO {

    private String refreshToken;
    private LocalDateTime expiration;
}
