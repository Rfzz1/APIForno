package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Temperatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemperaturaRepository extends JpaRepository<Temperatura, UUID> {
    Optional<Temperatura> findByRegistradoEm(LocalDateTime registradoEm);
}
