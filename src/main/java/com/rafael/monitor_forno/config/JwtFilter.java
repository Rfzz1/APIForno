package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.service.FornoDetailsService;
import com.rafael.monitor_forno.service.JwtService;
import com.rafael.monitor_forno.service.UsuarioDetailsService;
import io.jsonwebtoken.Claims;
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

    public JwtFilter(JwtService jwtService, UsuarioDetailsService usuarioDetailsService, FornoDetailsService fornoDetailsService) {
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
            Claims claims = jwtService.extrairTodasClaims(token);

            String subject = claims.getSubject();
            String tipo = claims.get("tipo", String.class);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails details = null;

                // Lógica limpa: Usa o tipo salvo no token para ir direto no banco certo
                try {
                    if ("FORNO".equals(tipo)) {
                        details = fornoDetailsService.loadUserByUsername(subject);
                    } else if ("USUARIO".equals(tipo)) {
                        details = usuarioDetailsService.loadUserByUsername(subject);
                    }
                } catch (UsernameNotFoundException e) {
                    System.out.println("Entidade não encontrada para: " + subject);
                }

                // Valida e autentica
                if (details != null && jwtService.tokenValido(token, details.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Se o token estiver expirado ou inválido, limpa o contexto
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}