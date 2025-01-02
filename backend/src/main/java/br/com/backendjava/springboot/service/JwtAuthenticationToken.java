package br.com.backendjava.springboot.service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.authentication.AbstractAuthenticationToken;

// Token personalizado

/**
 * JwtAuthenticationToken é uma classe de token personalizado e representa um token de autenticação JWT.
 * 
 * @param username O nome de usuário associado ao token de autenticação.
 * @param isAdmin Um valor booleano indicando se o usuário é um administrador.
 * @param authorities As autoridades concedidas ao usuário.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final Boolean isAdmin;

    public JwtAuthenticationToken(String username, Boolean isAdmin, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.isAdmin = isAdmin;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }
}