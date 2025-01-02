package br.com.backendjava.springboot.component;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.model.UserModel;
import br.com.backendjava.springboot.service.EncryptionService;

/**
 * Classe responsável por popular o banco de dados com dados iniciais.
 * 
 * Esta classe é um componente Spring que utiliza Hibernate para interagir com o banco de dados.
 * O método seed() é executado após a construção do bean e verifica se há usuários no banco de dados.
 * Se não houver, cria um usuário administrador e um usuário cliente com senhas criptografadas.
 * 
 * Métodos:
 * - seed(): Método que realiza a inserção dos dados iniciais no banco de dados.
 * 
 * Exceções:
 * - Em caso de erro durante a transação, a transação é revertida e a exceção é impressa no console.
 * 
 * OBS: Ocultar melhor dados em produção.
 */
@Component
public class DataSeeder {

    @PostConstruct
    public void seed(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        try{

            long userCount = (long) session.createQuery("SELECT COUNT(u) FROM UserModel u").uniqueResult();

            if (userCount == 0) {

                // User Admin

                UserModel userAdmin = new UserModel();

                userAdmin.setUsername("admin");
                userAdmin.setPassword(EncryptionService.encrypt("123qwe!@#"));
                userAdmin.setIsAdmin(true);

                // User Client

                UserModel userClient = new UserModel();

                userClient.setUsername("user");
                userClient.setPassword(EncryptionService.encrypt("123qwe123"));
                userClient.setIsAdmin(false);

                session.beginTransaction();

                session.persist(userAdmin);
                session.persist(userClient);

            }

            session.getTransaction().commit();
        
        } catch (Exception e){
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}