package com.rafael.monitor_forno.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long versaoUsuario; // <-- O campo extra que você precisa!
    private final UUID id;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long versaoUsuario, UUID id) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.versaoUsuario = versaoUsuario;
        this.id = id;
    }
}