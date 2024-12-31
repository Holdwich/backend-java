package br.com.backendjava.springboot.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    public boolean isAdmin(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
            return jwtAuthToken.getIsAdmin();
        }
        return false;
    }
}
