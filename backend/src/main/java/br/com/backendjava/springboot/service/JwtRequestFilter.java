package br.com.backendjava.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
       final String authorizationHeader = request.getHeader("Authorization");

        // Variáveis

        String username = null;
        String jwt = null;

        // Se possui header...

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            // pega o JWT e extrai o usuário

            jwt = authorizationHeader.substring(7);
            username = jwtService.extractUsername(jwt);
        }

        // Se o usuário não é nulo...

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Extrai a propriedade "IsAdmin" e monta um token no back para a utilização

            Boolean isAdmin = jwtService.extractIsAdmin(jwt);

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(username, isAdmin, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Executa

        chain.doFilter(request, response);
    }
}