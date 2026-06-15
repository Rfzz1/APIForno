package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemporizadorRepository extends JpaRepository<Temporizador, UUID> {
    Optional<Temporizador>
    findFirstByUsuarioAndExecutadoFalseOrderByHorarioFimAsc(Usuario usuario);
    List<Temporizador> findByUsuario(Usuario usuario);
    Optional<Temporizador> findByIdAndUsuario(UUID id, Usuario usuario);
}
