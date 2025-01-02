package br.com.backendjava.springboot.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

/**
 * Serviço responsável por manipular tokens JWT.
 */
@Service
public class JwtService {

    /**
     * Chave secreta usada para assinar o token.
     * Em produção, colocar no arquivo .env.
     */
    private String SECRET_KEY = "ChaveSecretaAqui";

    /**
     * Extrai o nome de usuário do token.
     *
     * @param token o token JWT
     * @return o nome de usuário extraído do token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai a informação se o usuário é administrador do token.
     *
     * @param token o token JWT
     * @return true se o usuário é administrador, false caso contrário
     */
    public Boolean extractIsAdmin(String token) {
        return extractClaim(token, claims -> claims.get("isAdmin", Boolean.class));
    }

    /**
     * Extrai uma informação específica do token.
     *
     * @param token o token JWT
     * @param claimsResolver função que resolve a informação desejada
     * @param <T> tipo da informação a ser extraída
     * @return a informação extraída do token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todas as informações do token.
     *
     * @param token o token JWT
     * @return todas as informações contidas no token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * Gera um token JWT.
     *
     * @param username o nome de usuário
     * @param isAdmin se o usuário é administrador
     * @return o token JWT gerado
     */
    public String generateToken(String username, Boolean isAdmin) {
        return Jwts.builder()
                .setSubject(username)
                .claim("isAdmin", isAdmin)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 36000000 millisegundos (10h)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Valida o token JWT.
     *
     * @param token o token JWT
     * @param username o nome de usuário
     * @return true se o token é válido, false caso contrário
     */
    public Boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    /**
     * Verifica se o token está expirado.
     *
     * @param token o token JWT
     * @return true se o token está expirado, false caso contrário
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token.
     *
     * @param token o token JWT
     * @return a data de expiração do token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
