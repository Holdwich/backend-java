package br.com.backendjava.springboot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Classe que representa o modelo de Email.
 * 
 * Atributos:
 * - id: Identificador único do email.
 * - cliente: Referência para o cliente associado ao email.
 * - email: Endereço de email.
 * 
 * Métodos:
 * - getId: Retorna o identificador do email.
 * - setId: Define o identificador do email.
 * - getCliente: Retorna o cliente associado ao email.
 * - setCliente: Define o cliente associado ao email.
 * - getEmail: Retorna o endereço de email.
 * - setEmail: Define o endereço de email.
 */
@Entity
@Table(name = "Emails")
public class EmailModel {

    // -- Atributos --

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cpf", nullable = false)
    @JsonBackReference
    private ClientModel cliente;

    private String email;

    // -- Getters e Setters --

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClientModel getCliente() {
        return cliente;
    }

    public void setCliente(ClientModel cliente) {
        this.cliente = cliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
