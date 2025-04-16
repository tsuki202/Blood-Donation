package org.example;

public class Main {
    public static void main(String[] args) {
        // Ініціалізуємо базу даних при запуску програми
        DatabaseManager.initializeDatabase();

        // Запускаємо авторизацію/реєстрацію
        new AuthManager().start();
    }
}