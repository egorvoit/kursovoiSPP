

package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MedicinePriceBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            // Обработка команды /start
            if (messageText.equalsIgnoreCase("/start")) {
                String welcomeMessage = "Добро пожаловать в PharmaPriceBot! Как я могу помочь вам?";
                sendResponse(chatId, welcomeMessage);
            }
            // Обработка приветствия
            else if (messageText.equalsIgnoreCase("привет")) {
                String responseMessage = "Я бот в разработке. Скоро я смогу помочь вам с ценами на лекарства!";
                sendResponse(chatId, responseMessage);
            }
            // Обработка других сообщений
            else {
                String priceInfo = getMedicinePrice(messageText);
                sendResponse(chatId, priceInfo);
            }
        }
    }

    private String getMedicinePrice(String medicineName) {
        String priceInfo = "Цена не найдена.";
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/yourdb", "user", "password")) {
            String query = "SELECT price FROM medicines WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, medicineName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                priceInfo = "Цена на " + medicineName + ": " + resultSet.getString("price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceInfo;
    }

    private void sendResponse(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "PharmaPricePO8Bot";
    }

    @Override
    public String getBotToken() {
        return "7975615650:AAH6RRoX_fvCPPCjr3Gk8Qhvby9OAKg0sh8";
    }
}
