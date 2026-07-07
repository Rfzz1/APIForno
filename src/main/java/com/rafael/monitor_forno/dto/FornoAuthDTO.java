package com.rafael.monitor_forno.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoAuthDTO {

    private String serialNumber;
    private String secret;

}
