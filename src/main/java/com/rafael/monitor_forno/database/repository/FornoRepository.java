package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FornoRepository extends JpaRepository<Forno, UUID> {

    Optional<Forno> findBySerialNumber(String serialNumber);
    List<Forno> findByUsuario(Usuario usuario);

}
