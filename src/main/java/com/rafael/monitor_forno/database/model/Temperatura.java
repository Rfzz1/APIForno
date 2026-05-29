package com.rafael.monitor_forno.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="temperatura")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Temperatura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double temperaturaAtual;

    @Column (nullable = false)
    private Double temperaturaUltima;

    @Column(nullable = false)
    private LocalDateTime registradoEm;

}
