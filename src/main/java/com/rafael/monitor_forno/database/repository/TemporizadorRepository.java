package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemporizadorRepository extends JpaRepository<Temporizador, UUID> {
    Optional<Temporizador> findFirstByFornoAndExecutadoFalseOrderByHorarioFimAsc(Forno forno);
    List<Temporizador> findByFornoUsuario(Usuario usuario);
    Optional<Temporizador> findByIdAndForno(UUID id, Forno forno);
    Optional<Temporizador> findByIdAndFornoUsuario(UUID id, Usuario usuario);
    List<Temporizador> findAllByFornoIdAndFornoUsuarioEmail(UUID fornoId, String email);
}
