package br.com.backendjava.springboot.controller;

import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.service.EncryptionService;
import br.com.backendjava.springboot.service.JwtService;
import br.com.backendjava.springboot.model.UserModel;

import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
public class UserController {

    private JwtService jwtService;

    // Rota para login
    @GetMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        UserModel user = null;

        try {

            //Criptografando senha para comparar com a senha no banco de dados
            String encryptedPassword = EncryptionService.encrypt(password);

            // Iniciando a transação
            session.beginTransaction();

            // Query para achar o usuário no banco de dados
            user = session.createQuery("FROM usuarios WHERE username = :username AND password = :password", UserModel.class)
                .setParameter("username", username)
                .setParameter("password", encryptedPassword)
                .uniqueResult();

            // Commita a transação
            session.getTransaction().commit();

            // Se achar...
            if (user != null) {
                // Retorna o token de autorização
                return jwtService.generateToken(user.getUsername(), user.getIsAdmin());
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

    // Rota para registro
    @PostMapping("/register")
    public UserModel register(@RequestParam String username, @RequestParam String password) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        UserModel user = new UserModel();

        try {

            //Criptografando senha para salvar no banco de dados
            String encryptedPassword = EncryptionService.encrypt(password);

            // Iniciando a transação
            session.beginTransaction();

            // Setando os valores do usuário
            user.setUsername(username);
            user.setPassword(encryptedPassword);

            // Salvando o usuário no banco de dados
            session.persist(user);

            // Commita a transação e retorna o usuário
            session.getTransaction().commit();

            return user;

        } catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno do servidor durante a requisição.");
        } finally {
            // Fecha a sessão
            session.close();
        }
            
    }
}
