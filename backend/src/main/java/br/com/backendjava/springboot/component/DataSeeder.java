package br.com.backendjava.springboot.component;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.model.UserModel;
import br.com.backendjava.springboot.service.EncryptionService;

// -- usuários iniciais (ocultar melhor em produção) --

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