package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class TokenEncryption {
    public static void main(String[] args) {
        try {
            String token = "ваш_токен_бота"; // Вставьте токен вашего бота
            String key = "1234567890123456"; // Ключ должен быть 16 символов для AES-128
            String encryptedToken = CryptoUtils.encrypt(token, key);

            // Запись зашифрованного токена в файл
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("token.txt"))) {
                writer.write(encryptedToken);
            }
            System.out.println("Зашифрованный токен записан в token.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}