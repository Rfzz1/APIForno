package com.rafael.monitor_forno.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporizadorResponseDTO {

   private UUID id;
   private Instant horarioInicio;
   private Instant horarioFim;
   private Long duracaoSegundos;
   private boolean executado;
}
