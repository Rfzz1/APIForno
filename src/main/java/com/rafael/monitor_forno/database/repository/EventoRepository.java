package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventoRepository extends JpaRepository<Evento, UUID> {

    Optional<Evento> findByIdAndUsuario(UUID id, Usuario usuario);
    List<Evento> findAllByUsuario(Usuario usuario);
    Optional<Evento> findFirstByUsuarioOrderByCriadoEmDesc(Usuario usuario);

}
