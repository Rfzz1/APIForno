package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.service.FornoDetailsService;
import com.rafael.monitor_forno.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final FornoDetailsService fornoDetailsService;
    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtFilter jwtFilter, FornoDetailsService fornoDetailsService, UsuarioDetailsService usuarioDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtFilter = jwtFilter;
        this.fornoDetailsService = fornoDetailsService;
        this.usuarioDetailsService = usuarioDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        // Passando os services diretamente nos construtores
        DaoAuthenticationProvider fornoProvider = new DaoAuthenticationProvider(fornoDetailsService);
        fornoProvider.setPasswordEncoder(passwordEncoder);

        DaoAuthenticationProvider usuarioProvider = new DaoAuthenticationProvider(usuarioDetailsService);
        usuarioProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(fornoProvider, usuarioProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST,"/v1/auth/login", "/v1/fornos/auth", "/v1/usuario", "/v1/auth/esqueci-minha-senha", "/v1/auth/redefinir-senha").permitAll()
                        .requestMatchers("/v1/temperaturas").hasRole("FORNO")
                        .requestMatchers(HttpMethod.POST, "/v1/usuario/**").hasAuthority("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
//sda