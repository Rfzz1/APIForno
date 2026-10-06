package com.rafael.monitor_forno.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoSilenciarBuzzerDTO {

    private boolean muted;
    private String serialNumber;
    private String acao;

}
