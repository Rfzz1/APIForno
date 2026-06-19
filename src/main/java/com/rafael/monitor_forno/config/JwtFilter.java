package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.service.FornoDetailsService;
import com.rafael.monitor_forno.service.JwtService;
import com.rafael.monitor_forno.service.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;
    private final FornoDetailsService fornoDetailsService;

    public JwtFilter(
            JwtService jwtService,
            UsuarioDetailsService usuarioDetailsService, FornoDetailsService fornoDetailsService) {

        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
        this.fornoDetailsService = fornoDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String subject = jwtService.extrairSubject(token);
            UserDetails details = null;

            // 1. Tenta carregar usuário ou forno
            try {
                details = usuarioDetailsService.loadUserByUsername(subject);
            } catch (UsernameNotFoundException e) {
                details = fornoDetailsService.loadUserByUsername(subject); // Se não é usuário, tenta forno
            }

            // 2. Valida o token e autentica apenas uma vez
            if (details != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Aqui garantimos que o token é válido para o subject encontrado
                if (jwtService.tokenValido(token, details.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Autenticado com sucesso: " + subject);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}