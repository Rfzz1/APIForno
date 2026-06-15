package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Telemetria;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TelemetriaRepository extends JpaRepository<Telemetria, UUID> {

    Optional<Telemetria> findFirstByUsuarioOrderByAtualizadoEmDesc(Usuario usuario);

}
