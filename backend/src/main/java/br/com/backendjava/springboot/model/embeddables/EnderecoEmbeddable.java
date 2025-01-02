package br.com.backendjava.springboot.model.embeddables;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe que representa um endereço embutido em outra entidade.
 * Utiliza a API ViaCEP para preencher automaticamente os campos
 * logradouro, bairro, cidade e uf com base no CEP fornecido.
 * 
 * Atributos:
 * - cep: Código de Endereçamento Postal (CEP) do endereço. (Obrigatório)
 * - logradouro: Nome da rua, avenida, etc. (Obrigatório)
 * - bairro: Nome do bairro. (Obrigatório)
 * - cidade: Nome da cidade. (Obrigatório)
 * - uf: Unidade Federativa (estado). (Obrigatório)
 * - complemento: Complemento do endereço. (Opcional)
 * 
 * Métodos:
 * - getCep(): Retorna o CEP do endereço.
 * - setCep(): Define o CEP do endereço e preenche automaticamente os demais campos.
 * - getLogradouro(): Retorna o logradouro do endereço.
 * - setLogradouro(): Define o logradouro do endereço.
 * - getBairro(): Retorna o bairro do endereço.
 * - setBairro(): Define o bairro do endereço.
 * - getCidade(): Retorna a cidade do endereço.
 * - setCidade(): Define a cidade do endereço.
 * - getUf(): Retorna a unidade federativa (estado) do endereço.
 * - setUf(): Define a unidade federativa (estado) do endereço.
 * - getComplemento(): Retorna o complemento do endereço.
 * - setComplemento(): Define o complemento do endereço.
 */
@Embeddable
public class EnderecoEmbeddable {

    // -- Atributos --

    @NotNull
    private String cep;

    @NotNull
    private String logradouro;

    @NotNull
    private String bairro;

    @NotNull
    private String cidade;

    @NotNull
    private String uf;

    private String complemento;

    // -- Getters e Setters --

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;

        // Acesso de API para pegar dados do CEP
        if (cep != null && !cep.isEmpty()) {
            try {
                URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                // Se código 200, pega os dados (somente se o método for chamado com o CEP formatado sem máscara)
                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    conn.disconnect();

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(sb.toString());

                    // Preenche os campos

                    this.logradouro = rootNode.path("logradouro").asText();
                    this.bairro = rootNode.path("bairro").asText();
                    this.cidade = rootNode.path("localidade").asText();
                    this.uf = rootNode.path("uf").asText();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
}
