package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.MensagemSuporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MensagemSuporteRepository extends JpaRepository<MensagemSuporte, UUID> {

   @Modifying
   @Query("UPDATE MensagemSuporte m SET m.remetente = null WHERE m.remetente.id = :usuarioId")
   void anularRemetentePorUsuarioId(@Param("usuarioId") UUID usuarioId);

}
