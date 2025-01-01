package br.com.backendjava.springboot.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import br.com.backendjava.springboot.HibernateUtil;
import br.com.backendjava.springboot.model.ClientModel;
import br.com.backendjava.springboot.model.PhoneModel;


@RestController
@EnableAutoConfiguration
@RequestMapping("/client")
public class ClientController {
    
    // Rota para pegar todos os clientes
    @GetMapping("/get")
    public List<ClientModel> getClient() {
        
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
            }

            else {
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
