package com.rafael.monitor_forno.service;

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
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "Usuário não encontrado"
                        )   
                );

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(usuario.getRole().name())
                .build();
    }
}
