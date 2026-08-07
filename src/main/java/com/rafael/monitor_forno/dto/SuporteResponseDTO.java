package com.rafael.monitor_forno.dto;

import com.rafael.monitor_forno.enums.suporte.Categoria;
import com.rafael.monitor_forno.enums.suporte.Status;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuporteResponseDTO {

    private UUID id;
    private Categoria categoria;
    private Status status;
    private String titulo;
    private List<SuporteMensagemResponseDTO> mensagens;

}
