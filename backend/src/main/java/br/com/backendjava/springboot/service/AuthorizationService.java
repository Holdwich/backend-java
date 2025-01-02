package br.com.backendjava.springboot.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Serviço de autorização responsável por verificar permissões de usuários.
 */
@Service
public class AuthorizationService {

    // Checa a propriedade "IsAdmin" do token

    public boolean isAdmin(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
            return jwtAuthToken.getIsAdmin();
        }
        return false;
    }
}
