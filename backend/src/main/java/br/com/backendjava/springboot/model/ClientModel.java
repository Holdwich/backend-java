package br.com.backendjava.springboot.model;

import br.com.backendjava.springboot.model.embeddables.EnderecoEmbeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Clientes")
public class ClientModel {

    // -- Atributos --

    @Id
    @NotBlank
    private Integer cpf;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @Embedded
    private EnderecoEmbeddable endereco;

    // -- Relacionamentos --

    @OneToMany(mappedBy = "cliente")
    private List<PhoneModel> telefones = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<EmailModel> emails = new ArrayList<>();

    // -- Getters e Setters --

    public Integer getCpf() {
        return cpf;
    }

    public void setCpf(Integer cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EnderecoEmbeddable getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoEmbeddable endereco) {
        this.endereco = endereco;
    }

    public List<PhoneModel> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<PhoneModel> telefones) {
        this.telefones = telefones;
    }

    public List<EmailModel> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailModel> emails) {
        this.emails = emails;
    }   

    // -- Métodos auxiliares --
    
    public void addTelefone(PhoneModel telefone) {
        telefone.setCliente(this);
        this.telefones.add(telefone);
    }

    public void removeTelefone(PhoneModel telefone) {
        telefone.setCliente(null);
        this.telefones.remove(telefone);
    }

    public void addEmail(EmailModel email) {
        email.setCliente(this);
        this.emails.add(email);
    }

    public void removeEmail(EmailModel email) {
        email.setCliente(null);
        this.emails.remove(email);
    }
}
