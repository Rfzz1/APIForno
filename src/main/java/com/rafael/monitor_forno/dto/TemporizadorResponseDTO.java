package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorResponseDTO {

   private UUID id;
   private LocalDateTime criadoEm;
   private LocalDateTime horarioFim;
   private boolean executado;
}
