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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.model.ClientModel;
import br.com.backendjava.springboot.model.PhoneModel;
import br.com.backendjava.springboot.model.embeddables.EnderecoEmbeddable;
import br.com.backendjava.springboot.model.EmailModel;




@RestController
@EnableAutoConfiguration
@RequestMapping("/client")
public class ClientController {
    
    /**
     * Endpoint para obter todos os clientes.
     *
     * @return Uma lista de objetos ClientModel com todos os clientes.
     * @throws ResponseStatusException Se ocorrer um erro interno do servidor durante a requisição.
     */
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

    /**
     * Endpoint para obter um cliente pelo CPF.
     *
     * @param cpf O CPF do cliente a ser buscado. Este parâmetro é obrigatório.
     * @return O modelo do cliente correspondente ao CPF fornecido, com os campos de CPF, CEP e telefone formatados para exibição.
     * @throws ResponseStatusException Se ocorrer um erro interno do servidor durante a requisição.
     */
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

    /**
     * Endpoint para registrar um novo cliente.
     *
     * @param objectNode JSON contendo os dados do cliente a ser registrado. Deve conter os seguintes campos:
     *                   - cpf (String): CPF do cliente (obrigatório).
     *                   - nome (String): Nome do cliente (obrigatório).
     *                   - cep (String): CEP do cliente (obrigatório).
     *                   - logradouro (String): Logradouro do endereço do cliente (opcional).
     *                   - bairro (String): Bairro do endereço do cliente (opcional).
     *                   - cidade (String): Cidade do endereço do cliente (opcional).
     *                   - uf (String): Unidade Federativa do endereço do cliente (opcional).
     *                   - complemento (String): Complemento do endereço do cliente (opcional).
     *                   - telefones (Array): Lista de telefones do cliente, onde cada telefone deve conter:
     *                       - tipo (String): Tipo do telefone (ex: "celular", "residencial").
     *                       - telefone (String): Número do telefone.
     *                   - emails (Array): Lista de emails do cliente, onde cada email deve conter:
     *                       - email (String): Endereço de email.
     * @return ClientModel Objeto do cliente registrado contendo os dados salvos.
     * @throws ResponseStatusException Se algum dos parâmetros obrigatórios (CPF, Nome, CEP) estiver ausente ou vazio.
     * @throws ResponseStatusException Se a lista de telefones estiver vazia.
     * @throws ResponseStatusException Se a lista de emails estiver vazia ou se algum email for inválido.
     * @throws ResponseStatusException Se ocorrer um erro interno do servidor durante a requisição.
     */
    @PostMapping("/register/client")
    public ClientModel registerClient(@RequestBody ObjectNode objectNode) {

        // ****** INICIALIZAÇÃO ******
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        ClientModel client = new ClientModel();
        EnderecoEmbeddable endereco = new EnderecoEmbeddable();

        String cpf = null;
        String nome = null;
        String cep = null;
        String logradouro = null;
        String bairro = null;
        String cidade = null;
        String uf = null;
        String complemento = null;

        client.setEndereco(endereco);

        // Pega os valores do JSON

        if(objectNode.has("cpf")){
            cpf = objectNode.get("cpf").asText();
        }

        if(objectNode.has("nome")){
            nome = objectNode.get("nome").asText();
        }

        if(objectNode.has("cep")){
            cep = objectNode.get("cep").asText();
        }

        if(objectNode.has("logradouro")){
            logradouro = objectNode.get("logradouro").asText();
        }

        if(objectNode.has("bairro")){
            bairro = objectNode.get("bairro").asText();
        }

        if(objectNode.has("cidade")){
            cidade = objectNode.get("cidade").asText();
        }

        if(objectNode.has("uf")){
            uf = objectNode.get("uf").asText();
        }

        if(objectNode.has("complemento")){
            complemento = objectNode.get("complemento").asText();
        }

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

            // Verifica se o email é válido
            
            String emailAddress = email.get("email").asText();
            if (!emailAddress.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido: " + emailAddress);
            }


            EmailModel emailModel = new EmailModel();
            emailModel.setEmail(emailAddress);
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

    /**
     * Endpoint para registrar um email para um cliente existente.
     *
     * @param objectNode Objeto JSON contendo os parâmetros "cpf" e "email".
     * @return EmailModel Objeto contendo os dados do email registrado.
     * @throws ResponseStatusException Se os parâmetros "cpf" ou "email" estiverem vazios ou nulos, ou se o email for inválido.
     * @throws ResponseStatusException Se ocorrer um erro interno do servidor durante a requisição.
     */
    @PostMapping("/register/email")
    public EmailModel registerEmail(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        EmailModel email = new EmailModel();
        ClientModel client = null;

        String cpf = null;
        String emailAddress = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("email")) {
            emailAddress = objectNode.get("email").asText();
        }

        // Verifica se CPF ou email estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF ou email não podem ser vazios.");
        }

        // Verifica se o email é válido
        if (!emailAddress.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido: " + emailAddress);
        }

        // Formatação do CPF (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o cliente pelo CPF
            Query query = session.createQuery("FROM ClientModel WHERE cpf = :cpf", ClientModel.class)
                .setParameter("cpf", cpf);

            client = (ClientModel) query.getSingleResult();

            // Setando os valores
            email.setEmail(emailAddress);
            email.setCliente(client);

            // Salvando email no banco de dados
            session.save(email);

            // Commitando a transação
            session.getTransaction().commit();

            return email;
        } 
        catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } 
        finally {
            // Fecha a sessão
            session.close();
        }
    }

    /**
     * Endpoint para registrar um telefone para um cliente.
     *
     * @param objectNode Objeto JSON contendo os parâmetros "cpf", "telefone" e "tipo".
     * @return PhoneModel Objeto representando o telefone registrado.
     * @throws ResponseStatusException Se os parâmetros "cpf", "telefone" ou "tipo" estiverem vazios ou nulos, ou se ocorrer um erro interno do servidor.
     */
    @PostMapping("/register/phone")
    public PhoneModel registerPhone(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        PhoneModel phone = new PhoneModel();
        ClientModel client = null;

        String cpf = null;
        String phoneNumber = null;
        String phoneType = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("telefone")) {
            phoneNumber = objectNode.get("telefone").asText();
        }

        if (objectNode.has("tipo")) {
            phoneType = objectNode.get("tipo").asText();
        }

        // Verifica se CPF, telefone ou tipo estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || phoneNumber == null || phoneNumber.trim().isEmpty() || phoneType == null || phoneType.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF, telefone ou tipo não podem ser vazios.");
        }

        // Formatação do CPF e telefone (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o cliente pelo CPF
            Query query = session.createQuery("FROM ClientModel WHERE cpf = :cpf", ClientModel.class)
                .setParameter("cpf", cpf);

            client = (ClientModel) query.getSingleResult();

            // Setando os valores
            phone.setTelefone(phoneNumber);
            phone.setTipo(phoneType);
            phone.setCliente(client);

            // Salvando telefone no banco de dados
            session.save(phone);

            // Commitando a transação
            session.getTransaction().commit();

            return phone;
        } 
        catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } 
        finally {
            // Fecha a sessão
            session.close();
        }
    }

    /**
     * Endpoint para deletar um cliente pelo CPF.
     * 
     * @param cpf O CPF do cliente a ser deletado. Este parâmetro é obrigatório.
     * @return HttpStatus OK se a operação for bem-sucedida, ou INTERNAL_SERVER_ERROR se ocorrer um erro.
     * @throws ResponseStatusException Se ocorrer um erro interno do servidor durante a requisição.
     */
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

    /**
     * Endpoint para deletar um email associado a um cliente.
     * 
     * @param objectNode Objeto JSON contendo os parâmetros "cpf" e "email".
     * @return HttpStatus OK se o email for deletado com sucesso, ou um erro apropriado se ocorrer algum problema.
     * @throws ResponseStatusException se os parâmetros "cpf" ou "email" estiverem vazios ou nulos, 
     *         se o cliente não tiver mais de um email cadastrado, ou se ocorrer um erro interno do servidor.
     */
    @PostMapping("/delete/email")
    public HttpStatus deleteEmail(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        long count = 0;
        EmailModel email = null;

        String cpf = null;
        String emailAddress = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("email")) {
            emailAddress = objectNode.get("email").asText();
        }

        // Verifica se CPF ou email estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF ou email não podem ser vazios.");
        }

        // Formatação do CPF (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para contar quantos emails existem no CPF
            query = session.createQuery("SELECT COUNT(*) FROM EmailModel WHERE cliente.cpf = :cpf", Long.class)
                .setParameter("cpf", cpf);

            count = (long) query.getSingleResult();

            // Se não tiver mais de 1 email cadastrado, não é possível deletar
            if (count <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível deletar o email (O cliente não tem, ou tem apenas um email cadastrado).");
            }

            // Query para pegar o email pelo CPF e endereço de email
            query = session.createQuery("FROM EmailModel WHERE cliente.cpf = :cpf AND email = :email", EmailModel.class)
                .setParameter("cpf", cpf)
                .setParameter("email", emailAddress);

            email = (EmailModel) query.getSingleResult();

            // Deletando email
            session.delete(email);

            // Commitando a transação
            session.getTransaction().commit();

            return HttpStatus.OK;
        } 
        catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } 
        finally {
            // Fecha a sessão
            session.close();
        }
    }
    
    /**
     * Endpoint para deletar um telefone de um cliente.
     * 
     * @param objectNode JSON contendo os parâmetros "cpf" e "telefone".
     * @return HttpStatus OK se o telefone for deletado com sucesso, ou um erro apropriado se ocorrer algum problema.
     * @throws ResponseStatusException se os parâmetros forem insuficientes, se o cliente não tiver mais de um telefone cadastrado, ou se ocorrer um erro interno do servidor.
     */
    @PostMapping("/delete/phone")
    public HttpStatus deletePhone(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        long count = 0;
        PhoneModel phone = null;

        String cpf = null;
        String phoneNumber = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("telefone")) {
            phoneNumber = objectNode.get("telefone").asText();
        }

        // Verifica se CPF ou telefone estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF ou telefone não podem ser vazios.");
        }

        // Formatação do CPF e telefone (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para contar quantos telefones existem no CPF
            query = session.createQuery("SELECT COUNT(*) FROM PhoneModel WHERE cliente.cpf = :cpf", Long.class)
                .setParameter("cpf", cpf);

            count = (long) query.getSingleResult();

            // Se não tiver mais de um telefone, não é possível deletar
            if (count <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível deletar o telefone (O cliente não tem, ou tem apenas um telefone cadastrado).");
            }

            // Query para pegar o telefone pelo CPF e número de telefone
            query = session.createQuery("FROM PhoneModel WHERE cliente.cpf = :cpf AND telefone = :telefone", PhoneModel.class)
                .setParameter("cpf", cpf)
                .setParameter("telefone", phoneNumber);

            phone = (PhoneModel) query.getSingleResult();

            // Deletando telefone
            session.delete(phone);

            // Commitando a transação
            session.getTransaction().commit();

            return HttpStatus.OK;
        } 
        catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } 
        finally {
            // Fecha a sessão
            session.close();
        }
    }

    /**
     * Endpoint para atualizar as informações de um cliente existente no banco de dados.
     *
     * @param objectNode JSON contendo os dados do cliente a serem atualizados. 
     *                   Os campos aceitos são: cpf, nome, cep, logradouro, bairro, cidade, uf, complemento.
     * @return O modelo do cliente atualizado.
     * @throws ResponseStatusException Se o CPF não for fornecido ou se ocorrer um erro interno do servidor.
     */
    @PostMapping("/update/client")
    public ClientModel updateClient(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        ClientModel client = null;

        String cpf = null;
        String nome = null;
        String cep = null;
        String logradouro = null;
        String bairro = null;
        String cidade = null;
        String uf = null;
        String complemento = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("nome")) {
            nome = objectNode.get("nome").asText();
        }

        if (objectNode.has("cep")) {
            cep = objectNode.get("cep").asText();
        }

        if (objectNode.has("logradouro")) {
            logradouro = objectNode.get("logradouro").asText();
        }

        if (objectNode.has("bairro")) {
            bairro = objectNode.get("bairro").asText();
        }

        if (objectNode.has("cidade")) {
            cidade = objectNode.get("cidade").asText();
        }

        if (objectNode.has("uf")) {
            uf = objectNode.get("uf").asText();
        }

        if (objectNode.has("complemento")) {
            complemento = objectNode.get("complemento").asText();
        }

        // Verifica se CPF está vazio ou nulo
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro insuficiente: CPF não pode ser vazio.");
        }

        // Formatação do CPF (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o cliente pelo CPF
            Query query = session.createQuery("FROM ClientModel WHERE cpf = :cpf", ClientModel.class)
                .setParameter("cpf", cpf);

            client = (ClientModel) query.getSingleResult();

            // Atualizando os valores
            if (nome != null) {
                client.setNome(nome);
            }
            if (cep != null) {
                client.getEndereco().setCep(cep.replaceAll("[^0-9]", ""));
            }
            if (logradouro != null) {
                client.getEndereco().setLogradouro(logradouro);
            }
            if (bairro != null) {
                client.getEndereco().setBairro(bairro);
            }
            if (cidade != null) {
                client.getEndereco().setCidade(cidade);
            }
            if (uf != null) {
                client.getEndereco().setUf(uf);
            }
            if (complemento != null) {
                client.getEndereco().setComplemento(complemento);
            }

            // Salvando cliente no banco de dados
            session.update(client);

            // Commitando a transação
            session.getTransaction().commit();

            return client;
        } 
        catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } 
        finally {
            // Fecha a sessão
            session.close();
        }
    }

    /**
     * Endpoint para atualizar o endereço de email de um cliente com base no CPF e no email antigo fornecidos.
     *
     * @param objectNode Objeto JSON contendo os parâmetros "cpf", "emailVelho" e "emailNovo".
     * @return O modelo de email atualizado.
     * @throws ResponseStatusException Se os parâmetros forem insuficientes, se o novo email for inválido,
     * ou se ocorrer um erro interno do servidor durante a requisição.
     */
    @PostMapping("/update/email")
    public EmailModel updateEmail(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        EmailModel email = null;
        Query query = null;

        String cpf = null;
        String newEmail = null;
        String oldEmail = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("emailVelho")) {
            oldEmail = objectNode.get("emailVelho").asText();
        }

        if (objectNode.has("emailNovo")) {
            newEmail = objectNode.get("emailNovo").asText();
        }

        // Verifica se CPF ou emails estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || oldEmail == null || oldEmail.trim().isEmpty() || newEmail == null || newEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF ou emails não podem ser vazios.");
        }

        // Verifica se o novo email é válido
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido: " + newEmail);
        }

        // Formatação do CPF (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o email pelo CPF e endereço de email antigo
            query = session.createQuery("FROM EmailModel WHERE cliente.cpf = :cpf AND email = :email", EmailModel.class)
                .setParameter("cpf", cpf)
                .setParameter("email", oldEmail);

            email = (EmailModel) query.getSingleResult();

            // Atualizando o email
            email.setEmail(newEmail);

            // Salvando email no banco de dados
            session.update(email);

            // Commitando a transação
            session.getTransaction().commit();

            return email;
        } 
        catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } 
        finally {
            // Fecha a sessão
            session.close();
        }

    }

    /**
     * Endpoint para atualizar o número de telefone de um cliente.
     *
     * @param objectNode Objeto JSON contendo os seguintes campos:
     *                   - "cpf": CPF do cliente (obrigatório)
     *                   - "telefoneVelho": Número de telefone antigo (obrigatório)
     *                   - "telefoneNovo": Novo número de telefone (obrigatório)
     *                   - "tipo": Tipo do telefone (obrigatório)
     * @return O modelo de telefone atualizado.
     * @throws ResponseStatusException Se os parâmetros forem insuficientes ou ocorrer um erro interno do servidor.
     */
    @PostMapping("/update/phone")
    public PhoneModel updatePhone(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        PhoneModel phone = null;
        Query query = null;

        String cpf = null;
        String newPhone = null;
        String oldPhone = null;
        String phoneType = null;

        // Pega os valores do JSON
        if (objectNode.has("cpf")) {
            cpf = objectNode.get("cpf").asText();
        }

        if (objectNode.has("telefoneVelho")) {
            oldPhone = objectNode.get("telefoneVelho").asText();
        }

        if (objectNode.has("telefoneNovo")) {
            newPhone = objectNode.get("telefoneNovo").asText();
        }

        if (objectNode.has("tipo")) {
            phoneType = objectNode.get("tipo").asText();
        }

        // Verifica se CPF, telefones ou tipo estão vazios ou nulos
        if (cpf == null || cpf.trim().isEmpty() || oldPhone == null || oldPhone.trim().isEmpty() || newPhone == null || newPhone.trim().isEmpty() || phoneType == null || phoneType.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: CPF, telefones ou tipo não podem ser vazios.");
        }

        // Formatação do CPF e telefones (removendo caracteres especiais)
        cpf = cpf.replaceAll("[^0-9]", "");
        oldPhone = oldPhone.replaceAll("[^0-9]", "");
        newPhone = newPhone.replaceAll("[^0-9]", "");

        try {
            // Iniciando a transação
            session.beginTransaction();

            // Query para pegar o telefone pelo CPF e número de telefone antigo
            query = session.createQuery("FROM PhoneModel WHERE cliente.cpf = :cpf AND telefone = :telefone", PhoneModel.class)
                .setParameter("cpf", cpf)
                .setParameter("telefone", oldPhone);

            phone = (PhoneModel) query.getSingleResult();

            // Atualizando o telefone
            phone.setTelefone(newPhone);
            phone.setTipo(phoneType);

            // Salvando telefone no banco de dados
            session.update(phone);

            // Commitando a transação
            session.getTransaction().commit();

            return phone;
        } 
        catch (Exception e) {
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

