package br.com.backendjava.springboot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * Classe que representa o modelo de usuário.
 * 
 * Atributos:
 * - id: Identificador único do usuário.
 * - username: Nome de usuário.
 * - password: Senha do usuário.
 * - isAdmin: Indica se o usuário é administrador.
 * 
 * Métodos:
 * - getId: Retorna o identificador do usuário.
 * - setId: Define o identificador do usuário.
 * - getUsername: Retorna o nome de usuário.
 * - setUsername: Define o nome de usuário.
 * - getPassword: Retorna a senha do usuário.
 * - setPassword: Define a senha do usuário.
 * - getIsAdmin: Retorna se o usuário é administrador.
 * - setIsAdmin: Define se o usuário é administrador.
 */
@Entity
@Table(name = "Usuarios")
public class UserModel {

    // -- Atributos --

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private Boolean isAdmin;

    // -- Getters e Setters --
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
