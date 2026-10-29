package com.rafael.monitor_forno;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        // Gera 32 bytes (256 bits) de entropia para o HS256
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);

        // Converte para Base64 para facilitar o armazenamento em arquivos de configuração
        String base64Secret = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("JWT Secret (Base64): " + base64Secret);
    }
}
