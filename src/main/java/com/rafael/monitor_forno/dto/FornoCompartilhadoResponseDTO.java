package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.enums.compartilhamento.StatusC;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornoCompartilhadoResponseDTO {

    private StatusC status;
    private String serialNumber;
    private String email;
    private LocalDateTime dataConvite;

}
