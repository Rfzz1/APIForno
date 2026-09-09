package com.rafael.monitor_forno.database.model;

import com.rafael.monitor_forno.enums.compartilhamento.StatusC;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="forno_compartilhado")
public class FornoCompartilhado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatusC status;

    @Column(nullable=false)
    private LocalDateTime dataConvite;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "forno_id")
    private Forno forno;
}