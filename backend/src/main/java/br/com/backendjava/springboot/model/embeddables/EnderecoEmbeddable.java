package br.com.backendjava.springboot.model.embeddables;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

                // Se código 200, pega os dados (útil para a máscara de CEP)
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
