package com.rafael.monitor_forno.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="refreshToken")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String refreshToken;

    private LocalDateTime expiracaoRefreshToken;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
