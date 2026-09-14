package com.rafael.monitor_forno.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoSilenciarBuzzerDTO {

    private boolean isMuted;
    private String serialNumber;
    private String acao;

}
