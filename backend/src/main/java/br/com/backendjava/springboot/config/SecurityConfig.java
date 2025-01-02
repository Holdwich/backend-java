package br.com.backendjava.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import br.com.backendjava.springboot.service.JwtRequestFilter;

/**
 * Configuração de segurança para a aplicação Spring Boot.
 * 
 * Esta classe configura a segurança da aplicação utilizando JWT para autenticação.
 * 
 * @param jwtRequestFilter Filtro de requisição JWT que será adicionado à cadeia de filtros de segurança.
 * 
 * @method securityFilterChain(): Configura a cadeia de filtros de segurança.
 * 
 * @param http Objeto HttpSecurity usado para configurar a segurança HTTP.
 * 
 * @return SecurityFilterChain configurado.
 * 
 * Configurações:
 * - Permite todas as requisições para endpoints que começam com "/user/**".
 * - Requer autenticação para o endpoint "/client/get" e "/client/get/all".
 * - Requer que qualquer outra requisição seja acessada apenas por administradores.
 * - Define a política de criação de sessão como STATELESS.
 * - Habilita CORS.
 * - Desabilita CSRF.
 * - Adiciona o filtro jwtRequestFilter antes do filtro UsernamePasswordAuthenticationFilter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/user/**").permitAll()
            .antMatchers("/client/get","/client/get/all").authenticated()
            .anyRequest().access("@authorizationService.isAdmin(authentication)")
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .cors()
            .and()
            .csrf().disable();
            

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
