package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Temporizador;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemporizadorRepository extends JpaRepository<Temporizador, UUID> {
    Optional<Temporizador> findFirstByFornoAndExecutadoFalseOrderByHorarioFimAsc(Forno forno);
    List<Temporizador> findByFornoUsuario(Usuario usuario);
    Optional<Temporizador> findByIdAndForno(UUID id, Forno forno);
    Optional<Temporizador> findByIdAndFornoUsuario(UUID id, Usuario usuario);


    @Query("""
        SELECT t FROM Temporizador t
        WHERE t.sessao.forno.id = :fornoId
        AND (
            t.sessao.forno.usuario.email = :email
            OR EXISTS (
                SELECT fc FROM FornoCompartilhado fc
                WHERE fc.forno.id = :fornoId
                AND fc.usuario.email = :email
                AND fc.status = 'ACEITO'
            )
        )
    """)
    List<Temporizador> findAllByFornoIdAndFornoUsuarioEmailOrCompartilhado(UUID fornoId, String email);


    List<Temporizador> findAllByExecutadoFalse();
    List<Temporizador> findAllByForno_Usuario(Usuario usuario);
}
