package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Evento;
import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.enums.eventos.EventoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventoRepository extends JpaRepository<Evento, UUID> {

    Optional<Evento> findByIdAndSessaoFornoUsuario(UUID id, Usuario usuario);

    List<Evento> findAllBySessaoFornoUsuario(Usuario usuario);

    Optional<Evento> findFirstBySessaoFornoIdOrderByCriadoEmDesc(UUID fornoId);

    List<Evento> findAllBySessaoFornoIdAndSessaoFornoUsuarioEmail(UUID fornoId, String email);

    List<Evento> findTopThreeBySessaoFornoIdAndSessaoFornoUsuarioEmailOrderByCriadoEmDesc(UUID fornoId, String email);

    @Query("""
    SELECT COUNT(e)
    FROM Evento e
    WHERE e.sessao.forno.id = :fornoId
        AND e.tipo IN :tipos
    """)
    Long countEventosBySessaoFornoIdAndTipoIn(UUID fornoId, List<EventoSistema> tipos);

    void deleteAllBySessaoFornoId(UUID fornoId);

}
