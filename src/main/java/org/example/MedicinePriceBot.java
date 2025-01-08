package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MedicinePriceBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            // Обработка команды /start
            if (messageText.equalsIgnoreCase("/start")) {
                String welcomeMessage = "Добро пожаловать в PharmaPriceBot! Напишите 'Категории', чтобы увидеть доступные категории или напишите название лекарства, чтобы узнать его цену.";
                sendResponse(chatId, welcomeMessage);
            }
            // Обработка запроса категорий
            else if (messageText.equalsIgnoreCase("категории")) {
                String categories = getCategories();
                sendResponse(chatId, categories);
            }
            // Обработка ввода категории
            else if (isCategoryInput(messageText)) {
                String medicinesInfo = getMedicinesByCategory(messageText);
                sendResponse(chatId, medicinesInfo + "\nТеперь вы можете ввести название лекарства, чтобы узнать его цену или введите название категории, чтобы узнать лекарства в этой категории.");
            }
            // Обработка запроса цены на лекарство
            else {
                String priceInfo = getMedicinePrice(messageText);
                sendResponse(chatId, priceInfo + "\nВы можете ввести другое название лекарства или введите название категории, чтобы узнать лекарства в этой категории.");
            }
        }
    }

    private boolean isCategoryInput(String input) {
        List<String> categories = getCategoriesList();
        return categories.contains(input);
    }

    private List<String> getCategoriesList() {
        List<String> categories = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "root", "1256")) {
            String query = "SELECT DISTINCT category FROM medicines";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    private String getCategories() {
        StringBuilder categories = new StringBuilder("Доступные категории:\n");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "root", "1256")) {
            String query = "SELECT DISTINCT category FROM medicines";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                categories.append(resultSet.getString("category")).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка получения категорий.";
        }
        return categories.toString();
    }

    private String getMedicinesByCategory(String category) {
        StringBuilder medicinesInfo = new StringBuilder("Лекарства в категории \"" + category + "\":\n");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "root", "1256")) {
            String query = "SELECT name FROM medicines WHERE category = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            boolean hasMedicines = false;
            while (resultSet.next()) {
                medicinesInfo.append(resultSet.getString("name")).append("\n");
                hasMedicines = true;
            }
            if (!hasMedicines) {
                medicinesInfo.append("Нет лекарств в этой категории.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка получения лекарств.";
        }
        return medicinesInfo.toString();
    }

    private String getMedicinePrice(String medicineName) {
        String priceInfo = "Цена не найдена.";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "root", "1256")) {
            String query = "SELECT price, category FROM medicines WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, medicineName);

            // Логирование названия лекарства
            System.out.println("Запрос цены для: " + medicineName);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String price = resultSet.getString("price");
                String category = resultSet.getString("category");
                priceInfo = "Цена на " + medicineName + ": " + price + "\nКатегория: " + category;
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
        try {
            String encryptedToken = readTokenFromFile("token.txt"); // Чтение токена из файла
            String key = "1234567890123456"; // Ключ для шифрования/дешифрования
            return CryptoUtils.decrypt(encryptedToken, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Или выбросьте исключение
    }

    private String readTokenFromFile(String filePath) {
        StringBuilder token = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                token.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token.toString();
    }
}