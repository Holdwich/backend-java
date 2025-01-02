package br.com.backendjava.springboot.controller;

import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.service.EncryptionService;
import br.com.backendjava.springboot.service.JwtService;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.hibernate.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")
    public String login(@RequestBody ObjectNode objectNode) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        boolean isAdmin = false;
        long count = 0;
        String username = null;
        String senha = null;

        // Extraindo os valores de username e senha do ObjectNode
        if (objectNode.has("username")){
            username = objectNode.get("username").asText();
        }

        if (objectNode.has("senha")){
            senha = objectNode.get("senha").asText();
        }

        // Checando se os valores foram passados ou não
        if (username == null || username.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros insuficientes: username ou senha não podem ser vazios.");
        }

        /******************************************************************************************************************
         * * OBS: Não utilizei o objeto diretamente por conta de um erro de typeCast cujo não consegui resolver a tempo * *
         ******************************************************************************************************************/

        try {

            //Criptografando senha para comparar com a senha no banco de dados
            String encryptedPassword = EncryptionService.encrypt(senha);

            // Iniciando a transação
            session.beginTransaction();

            // Query para achar o usuário no banco de dados
            query = session.createQuery("SELECT COUNT(*) FROM UserModel WHERE username = :username AND password = :senha")
                .setParameter("username", username)
                .setParameter("senha", encryptedPassword);

            count = (long) query.getSingleResult();

            // Se achar...
            if (count > 0) {
                // Busca coluna isAdmin do usuário
                query = session.createQuery("SELECT isAdmin FROM UserModel WHERE username = :username")
                    .setParameter("username", username);

                isAdmin = (boolean) query.getSingleResult();

                // Monta o token
                String token = jwtService.generateToken(username, isAdmin);

                // Retorna o token de autorização
                return "{\"token\": \"" + token + "\"}";
            } else {
                // Se não achar, retorna mensagem
                return "{\"message\": \"Usuário ou senha inválidos.\"}";
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
}
