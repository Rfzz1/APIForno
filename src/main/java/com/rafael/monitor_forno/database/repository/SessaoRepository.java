package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessaoRepository extends JpaRepository<Sessao, UUID> {
}
