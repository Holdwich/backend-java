package br.com.backendjava.springboot.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * Classe que representa o modelo de telefone.
 * 
 * Atributos:
 * - id: Identificador único do telefone.
 * - cliente: Cliente associado ao telefone.
 * - tipo: Tipo do telefone (ex: celular, residencial).
 * - telefone: Número do telefone.
 * 
 * Métodos:
 * - getId(): Retorna o identificador do telefone.
 * - setId(Integer id): Define o identificador do telefone.
 * - getCliente(): Retorna o cliente associado ao telefone.
 * - setCliente(ClientModel cliente): Define o cliente associado ao telefone.
 * - getTipo(): Retorna o tipo do telefone.
 * - setTipo(String tipo): Define o tipo do telefone.
 * - getTelefone(): Retorna o número do telefone.
 * - setTelefone(String telefone): Define o número do telefone.
 */
@Entity
@Table(name = "Telefones")
public class PhoneModel {

    // -- Atributos --

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cpf", nullable = false)
    @JsonBackReference
    private ClientModel cliente;

    private String tipo;
    private String telefone;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
}
