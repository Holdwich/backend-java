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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtService jwtService;

    // Rota para login
    @GetMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = null;
        boolean isAdmin = false;
        long count = 0;

        /******************************************************************************************************************
         * * OBS: Não utilizei o objeto diretamente por conta de um erro de typeCast cujo não consegui resolver a tempo * *
         ******************************************************************************************************************/

        try {

            //Criptografando senha para comparar com a senha no banco de dados
            String encryptedPassword = EncryptionService.encrypt(password);

            // Iniciando a transação
            session.beginTransaction();

            // Query para achar o usuário no banco de dados
            query = session.createQuery("SELECT COUNT(*) FROM UserModel WHERE username = :username AND password = :password")
                .setParameter("username", username)
                .setParameter("password", encryptedPassword);

            count = (long) query.getSingleResult();

            System.out.println(count);

            // Se achar...
            if (count > 0) {
                // Busca coluna isAdmin do usuário
                query = session.createQuery("SELECT isAdmin FROM UserModel WHERE username = :username")
                    .setParameter("username", username);

                isAdmin = (boolean) query.getSingleResult();

                System.out.println(isAdmin);

                // Monta o token

                String token = jwtService.generateToken(username, isAdmin);

                // Retorna o token de autorização
                return "{\"token\": \"" + token + "\"}";
            } else {
                // Se não achar, retorna null
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
}
