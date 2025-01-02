package br.com.backendjava.springboot.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.model.ClientModel;
import br.com.backendjava.springboot.model.PhoneModel;
import br.com.backendjava.springboot.model.embeddables.EnderecoEmbeddable;
import br.com.backendjava.springboot.model.EmailModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@EnableAutoConfiguration
@RequestMapping("/client")
public class ClientController {
    
    // Rota para pegar todos os clientes
    @GetMapping("/get/all")
    public List<ClientModel> getClients() {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        List<ClientModel> clients = null;
            
        try {
            
            // Iniciando a transação
            session.beginTransaction();
            
            // Query para pegar todos os clientes
            query = session.createQuery("FROM ClientModel", ClientModel.class);

            // Colocando na lista
            clients = query.getResultList();

            // Colocando as máscaras para exibição nos campos de CPF, CEP e Telefone

            if (clients != null) {

                for (ClientModel client : clients) {

                    // Inicializando os telefones e emails
                    Hibernate.initialize(client.getTelefones());
                    Hibernate.initialize(client.getEmails());

                    // CPF
                    String cpfString = client.getCpf();
                    String cpfMasked = cpfString.substring(0, 3) + "." + cpfString.substring(3, 6) + "." + cpfString.substring(6, 9) + "-" + cpfString.substring(9, 11);
                    client.setCpf(cpfMasked);
    
                    // CEP
                    String cepString = client.getEndereco().getCep();
                    String cepMasked = cepString.substring(0, 5) + "-" + cepString.substring(5, 8);
                    client.getEndereco().setCep(cepMasked);
    
                    // Telefone
                    for (PhoneModel phone : client.getTelefones()) {
                        String phoneString = phone.getTelefone();
                        String phoneType = phone.getTipo();

                        // Se for celular
                        if (phoneType.equals("celular")) {
                            String phoneMasked = "(" + phoneString.substring(0, 2) + ") " + phoneString.substring(2, 7) + "-" + phoneString.substring(7, 11);
                            phone.setTelefone(phoneMasked);
                        }
                        // Se não
                        else {
                            String phoneMasked = "(" + phoneString.substring(0, 2) + ") " + phoneString.substring(2, 6) + "-" + phoneString.substring(6, 10);
                            phone.setTelefone(phoneMasked);
                        }
                    }
                }

                return clients;
            } else {
                return null;
            }
        }
        catch (Exception e){
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        }
        finally {
            // Fecha a sessão
            session.close();
        }
    }

    // Rota para pegar um cliente específico pelo ID
    @GetMapping("/get")
    public ClientModel getClientById(@RequestParam(required = true) String cpf) {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        ClientModel client = null;

        // Formatação do CPF (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");
            
        try {

            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o cliente pelo CPF
            query = session.createQuery("FROM ClientModel WHERE cpf = :cpf", ClientModel.class)
                .setParameter("cpf", cpf);

            client = (ClientModel) query.getSingleResult();


            // Inicializando os telefones e emails
            Hibernate.initialize(client.getTelefones());
            Hibernate.initialize(client.getEmails());

            // Colocando as máscaras para exibição nos campos de CPF, CEP e Telefone

            if (client != null) {

                // CPF
                String cpfString = client.getCpf();
                String cpfMasked = cpfString.substring(0, 3) + "." + cpfString.substring(3, 6) + "." + cpfString.substring(6, 9) + "-" + cpfString.substring(9, 11);
                client.setCpf(cpfMasked);

                // CEP
                String cepString = client.getEndereco().getCep();
                String cepMasked = cepString.substring(0, 5) + "-" + cepString.substring(5, 8);
                client.getEndereco().setCep(cepMasked);

                // Telefone
                for (PhoneModel phone : client.getTelefones()) {
                    String phoneString = phone.getTelefone();
                    String phoneType = phone.getTipo();

                    // Se for celular
                    if (phoneType.equals("celular")) {
                        String phoneMasked = "(" + phoneString.substring(0, 2) + ") " + phoneString.substring(2, 7) + "-" + phoneString.substring(7, 11);
                        phone.setTelefone(phoneMasked);
                    }
                    // Se não
                    else {
                        String phoneMasked = "(" + phoneString.substring(0, 2) + ") " + phoneString.substring(2, 6) + "-" + phoneString.substring(6, 10);
                        phone.setTelefone(phoneMasked);
                    }
                }

                return client;
            } else {
                return null;
            }
        } 
        catch (Exception e){
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        }
        finally {
            // Fecha a sessão
            session.close();
        }
    }

    // Rota para cadastrar um cliente
    @PostMapping("/register")
    public ClientModel registerClient(@RequestBody ObjectNode objectNode) {

        // ****** INICIALIZAÇÃO ******
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        ClientModel client = new ClientModel();
        EnderecoEmbeddable endereco = new EnderecoEmbeddable();

        client.setEndereco(endereco);

        // Pega os valores do JSON

        String cpf = objectNode.get("cpf").asText();
        String nome = objectNode.get("nome").asText();
        String cep = objectNode.get("cep").asText();
        String logradouro = objectNode.get("logradouro") != null ? objectNode.get("logradouro").asText() : null;
        String bairro = objectNode.get("bairro") != null ? objectNode.get("bairro").asText() : null;
        String cidade = objectNode.get("cidade") != null ? objectNode.get("cidade").asText() : null;
        String uf = objectNode.get("uf") != null ? objectNode.get("uf").asText() : null;
        String complemento = objectNode.get("complemento") != null ? objectNode.get("complemento").asText() : null;

        // Verifica se CPF, Nome ou CEP estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || nome == null || nome.trim().isEmpty() || cep == null || cep.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF, Nome ou CEP não podem ser vazios.");
        }

        // Converte JSON de telefones para List<PhoneModel>
        List<PhoneModel> telefones = StreamSupport.stream(objectNode.get("telefones").spliterator(), false)
            .map(phone -> {
                PhoneModel phoneModel = new PhoneModel();
                phoneModel.setTipo(phone.get("tipo").asText());
                phoneModel.setTelefone(phone.get("telefone").asText());
                phoneModel.setCliente(client);
                return phoneModel;
            }).collect(Collectors.toList());

        // Verifica se a lista de telefones está vazia
        if (telefones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro insuficiente: Lista de telefones não pode ser vazia.");
        }

        // Converte JSON de emails para List<EmailModel>
        List<EmailModel> emails = StreamSupport.stream(objectNode.get("emails").spliterator(), false)
            .map(email -> {
                EmailModel emailModel = new EmailModel();
                emailModel.setEmail(email.get("email").asText());
                emailModel.setCliente(client);
                return emailModel;
            }).collect(Collectors.toList());

        // Verifica se a lista de emails está vazia
        if (emails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro insuficiente: Lista de emails não pode ser vazia.");
        }
            
        try {

            // ****** LÓGICA ******

            // Iniciando a transação
            session.beginTransaction();

            // Formatação do CPF, CEP e Telefone (removendo caracteres especiais)
            cpf = cpf.replaceAll("[^0-9]", "");
            cep = cep.replaceAll("[^0-9]", "");
            telefones.forEach(phone -> phone.setTelefone(phone.getTelefone().replaceAll("[^0-9]", "")));

            // Setando os valores
            client.setCpf(cpf);
            client.setNome(nome);
            client.getEndereco().setCep(cep);

            // Caso o logradouro, bairro, cidade, uf e complemento não sejam passados, deixa da forma que está (pego automaticamente)
            if (logradouro != null){
                client.getEndereco().setLogradouro(logradouro);
            }
            if (bairro != null){
                client.getEndereco().setBairro(bairro);
            }
            if (cidade != null){
                client.getEndereco().setCidade(cidade);
            }
            if (uf != null){
                client.getEndereco().setUf(uf);
            }

            // Adiciona complemento, caso exista
            if (complemento != null) {
                client.getEndereco().setComplemento(complemento);
            }

            // Salvando cliente no banco de dados
            session.save(client);

            // Salvando telefones e emails no banco de dados
            telefones.forEach(phone -> session.save(phone));
            emails.forEach(email -> session.save(email));


            // Adiciona telefones e emails
            client.setTelefones(telefones);
            client.setEmails(emails);

            // Commitando a transação
            session.getTransaction().commit();

            return client;
        } 
        catch (Exception e){
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        }
        finally {
            // Fecha a sessão
            session.close();
        }

    }

    @DeleteMapping("/delete")
    public HttpStatus deleteClient(@RequestParam(required = true) String cpf) {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        ClientModel client = null;

        // Formatação do CPF (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");
            
        try {

            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o cliente pelo CPF
            query = session.createQuery("FROM ClientModel WHERE cpf = :cpf", ClientModel.class)
                .setParameter("cpf", cpf);

            client = (ClientModel) query.getSingleResult();

            // Deletando telefones e emails associados
            for (PhoneModel phone : client.getTelefones()) {
                session.delete(phone);
            }
            for (EmailModel email : client.getEmails()) {
                session.delete(email);
            }

            // Deletando cliente
            session.delete(client);

            // Commitando a transação

            session.getTransaction().commit();

            return HttpStatus.OK;
        } 
        catch (Exception e){
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        }
        finally {
            // Fecha a sessão
            session.close();
        }
    }
}

