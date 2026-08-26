package com.rafael.monitor_forno.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long versaoUsuario; // <-- O campo extra que você precisa!

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long versaoUsuario) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.versaoUsuario = versaoUsuario;
    }
}