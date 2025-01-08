package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) {
        
        try {
            // Создаем экземпляр API
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Регистрируем бота
            botsApi.registerBot(new MedicinePriceBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}