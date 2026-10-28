package com.rafael.monitor_forno.database.repository;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.dto.TemporizadorResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    //Será usado pelo login e pelo spring security
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByTokenRecuperacaoSenha(String token);
    Optional<Usuario> findByEmailAnterior(String emailAnterior);
}
