package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.FotoPerfil;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FotoPerfilRepository extends JpaRepository<FotoPerfil, UUID> {
    Optional<FotoPerfil> findByUsuario(Usuario usuario);
}
