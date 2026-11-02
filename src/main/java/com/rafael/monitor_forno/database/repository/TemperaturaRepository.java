package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemperaturaRepository extends JpaRepository<Temperatura, UUID> {

    List<Temperatura> findByRegistradoEmBetweenAndSessaoForno(Instant inicio, Instant fim, Forno fornoU);

    Optional<Temperatura> findByIdAndSessaoFornoId(UUID id, UUID fornoId);

    List<Temperatura> findAllBySessaoFornoUsuario(Usuario usuario);

    List<Temperatura> findAllBySessaoFornoIdAndSessaoFornoUsuarioEmail(UUID fornoId, String email);

    Optional<Temperatura> findTopBySessaoOrderByRegistradoEmDesc(Sessao sessao);

    List<Temperatura> findAllBySessaoId(UUID sessaoId);

    @Query("""
    SELECT MAX(t.temperaturaAtual)
    FROM Temperatura t
    WHERE t.sessao.forno.id = :fornoId
    """)
    Double findTemperaturaMaximaByFornoId(UUID fornoId);

    //TemperaturaMaxima de Sessao
    @Query("""
    SELECT MAX(t.temperaturaAtual)
    FROM Temperatura t
    WHERE t.sessao.id = :sessaoId
    """)
    Double findTemperaturaMaximaBySessaoId(UUID sessaoId);

    void deleteAllBySessaoFornoId(UUID fornoId);

}
