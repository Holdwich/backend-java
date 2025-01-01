package br.com.backendjava.springboot.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private String SECRET_KEY = "ChaveSecretaAqui"; // Em produção, colocar no .env

    //  Extrai informações do token (Username) 
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //  Extrai informações do token (isAdmin) 
    public Boolean extractIsAdmin(String token) {
        return extractClaim(token, claims -> claims.get("isAdmin", Boolean.class));
    }

    //  Extrai informações do token 
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //  Extrai todas informações do token 
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    //  Gera token 
    public String generateToken(String username, Boolean isAdmin) {
        return Jwts.builder()
                .setSubject(username)
                .claim("isAdmin", isAdmin)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 36000000 millisegundos (10h)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    //  Valida token 
    public Boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    //  Verifica se token está expirado 
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //  Extrai data de expiração do token 
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
}
