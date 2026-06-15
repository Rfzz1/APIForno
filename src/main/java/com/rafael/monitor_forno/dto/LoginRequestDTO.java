package com.rafael.monitor_forno.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    private String email;
    private String senha;
}
