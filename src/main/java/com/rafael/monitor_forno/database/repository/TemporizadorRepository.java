package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Temporizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TemporizadorRepository extends JpaRepository<Temporizador, UUID> {
    Optional<Temporizador> findFirstByExecutadoFalseOrderByHorarioFimAsc();
}
