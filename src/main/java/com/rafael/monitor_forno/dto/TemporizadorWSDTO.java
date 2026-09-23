package com.rafael.monitor_forno.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorWSDTO {

    private Long duracaoSegundos;
    private String acao;

}
