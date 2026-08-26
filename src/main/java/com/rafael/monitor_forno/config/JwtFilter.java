package com.rafael.monitor_forno.config;

import com.rafael.monitor_forno.service.FornoDetailsService;
import com.rafael.monitor_forno.service.JwtService;
import com.rafael.monitor_forno.service.UsuarioDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

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

            // 1. Extraímos a versão que está guardada dentro do Token JWT
            Long versaoToken = claims.get("versao", Long.class);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails details = null;

                try {
                    if ("FORNO".equals(tipo)) {
                        details = fornoDetailsService.loadUserByUsername(subject);
                    } else if ("USUARIO".equals(tipo)) {
                        details = usuarioDetailsService.loadUserByUsername(subject);
                    }
                } catch (UsernameNotFoundException e) {
                    System.out.println("Entidade não encontrada para: " + subject);
                }

                if (details != null && jwtService.tokenValido(token, details.getUsername())) {

                    // 2. Fazemos a validação da versão se for um Usuário
                    boolean versaoValida = true;
                    if (details instanceof CustomUserDetails) {
                        CustomUserDetails customUser = (CustomUserDetails) details;
                        // Compara a versão do banco com a versão que veio no token
                        if (!Objects.equals(customUser.getVersaoUsuario(), versaoToken)) {
                            versaoValida = false;
                        }
                    }

                    // 3. Se o token for válido E a versão bater, autentica no sistema!
                    if (versaoValida) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}