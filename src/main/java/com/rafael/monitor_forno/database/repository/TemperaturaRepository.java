package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemperaturaRepository extends JpaRepository<Temperatura, UUID> {
    Optional<Temperatura> findByRegistradoEm(LocalDateTime registradoEm);
    Optional<Temperatura> findByIdAndUsuario(UUID id, Usuario usuario);
    List<Temperatura> findAllByUsuario(Usuario usuario);

    @Query("""
    SELECT MAX(t.temperaturaAtual)
    FROM Temperatura t
    WHERE t.sessao.usuario = :usuario
    """)
    Double findTemperaturaMaximaByUsuario(Usuario usuario);
}
