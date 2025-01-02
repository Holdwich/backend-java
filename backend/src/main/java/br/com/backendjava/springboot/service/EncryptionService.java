package br.com.backendjava.springboot.service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Serviço de criptografia utilizando o algoritmo AES.
 * 
 * Esta classe fornece métodos para criptografar e descriptografar dados usando uma chave simétrica.
 * 
 * Nota: Em produção, a chave deve ser armazenada em um arquivo .env e não embutida no código-fonte.
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "1234567890123456".getBytes(); // Em produção, colocar no .env

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
    
}
