package br.com.backendjava.springboot.service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.authentication.AbstractAuthenticationToken;

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