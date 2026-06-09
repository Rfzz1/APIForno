package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Telemetria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TelemetriaRepository extends JpaRepository<Telemetria, UUID> {


}
