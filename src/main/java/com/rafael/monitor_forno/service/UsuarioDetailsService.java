package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.config.CustomUserDetails;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Criamos a lista de permissões (Role) corretamente
        var authorities = java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(usuario.getRole().name()));

        // Retorna a nossa classe customizada com a versão inclusa
        return new CustomUserDetails(
                usuario.getEmail(),
                usuario.getSenha(),
                authorities,
                usuario.getVersaoUsuario() // <-- Pegando do banco
        );
    }
}
