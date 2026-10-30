package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface    SessaoRepository extends JpaRepository<Sessao, UUID> {

    Optional<Sessao> findByIdAndForno(UUID id, Forno forno);

    Optional<Sessao> findByIdAndFornoUsuario(UUID id, Usuario usuario);

    List<Sessao> findAllByFornoUsuario(Usuario usuario);

    List<Sessao> findAllByFornoId(UUID id);

    List<Sessao> findAllByFornoIdAndFornoUsuarioEmail(UUID id, String email);

    List<Sessao> findAllByFornoUsuarioAndInicioSessaoBetween(Usuario usuario, Instant dataInicio, Instant dataFim);

    int countByFornoId(UUID fornoId);

    @Query("""
    SELECT SUM(s.duracaoSegundos)
    FROM Sessao s
    WHERE s.forno.id = :fornoId
    """)
    Long findTempoTotalLigadoByFornoId(UUID fornoId);
}
