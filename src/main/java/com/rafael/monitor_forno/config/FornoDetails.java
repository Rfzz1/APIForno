package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.database.model.Forno;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class FornoDetails implements UserDetails {
    private final Forno forno;

    public FornoDetails(Forno forno) {
        this.forno = forno;
    }

    @Override
    public String getUsername() {
        return forno.getSerialNumber(); // O "nome" do forno é o serial
    }

    @Override
    public String getPassword() {
        return forno.getDeviceSecret(); // O secret funciona como senha
    }

    // Métodos abaixo são obrigatórios pela interface,
    // mas para o forno podemos retornar sempre true
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_FORNO"));
    }
}
