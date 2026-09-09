package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.FornoCompartilhado;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.enums.compartilhamento.StatusC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FornoCompartilhadoRepository extends JpaRepository<FornoCompartilhado, UUID> {

    Optional<FornoCompartilhado> findByFornoAndUsuario(Forno forno, Usuario usuario);
    List<FornoCompartilhado> findAllByUsuarioAndStatus(Usuario usuario, StatusC status);

}
