package br.com.backendjava.springboot;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Classe utilitária para configurar e fornecer a fábrica de sessões do Hibernate.
 *
 * Esta classe inicializa a fábrica de sessões a partir do arquivo de configuração
 * hibernate.cfg.xml e fornece métodos para obter a fábrica de sessões e fechar
 * a conexão com o banco de dados.
 * 
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Cria o session factory do hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Caso ocorra um erro, imprime a exceção e lança um erro
            System.err.println("Criação inicial do SessionFactory Falhou: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Fecha a conexão com o banco de dados
        getSessionFactory().close();
    }
}