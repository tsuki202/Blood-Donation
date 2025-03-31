package org.example;

import java.util.*;
import java.io.*;


public class Registration {
    private static final String FILE_NAME = "users.txt";
    private final List<UserData> users;

    public Registration() {
        users = new ArrayList<>();
        loadUsers();
    }

    public void register(String username, String password, String role) {
        if (userExists(username)) {
            System.out.println("⚠ Користувач з таким логіном вже існує!");
            return;
        }

        users.add(new UserData(username, password, role));
        saveUsers();
        System.out.println("✅ Реєстрація успішна!");
    }

    public boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.username().equals(username));
    }

    public String getPassword(String username) {
        return users.stream()
                .filter(user -> user.username().equals(username))
                .map(UserData::password)
                .findFirst()
                .orElse(null);
    }

    public String getRole(String username) {
        return users.stream()
                .filter(user -> user.username().equals(username))
                .map(UserData::role)
                .findFirst()
                .orElse(null);
    }

    public void deleteUser(String username) {
        if (!userExists(username)) {
            System.out.println("❌ Користувач не знайдений!");
            return;
        }

        users.removeIf(user -> user.username().equals(username));
        saveUsers();
        System.out.println("✅ Користувача " + username + " видалено.");
    }

    public void listUsers() {
        if (users.isEmpty()) {
            System.out.println("📌 Немає зареєстрованих користувачів.");
            return;
        }
        users.forEach(user -> System.out.println("👤 Логін: " + user.username() + ", Роль: " + user.role()));
    }

    private void saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(users);
        } catch (IOException e) {
            System.out.println("❌ Помилка збереження користувачів: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users.addAll((List<UserData>) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Помилка завантаження користувачів: " + e.getMessage());
        }
    }

    // Внутрішній клас для зберігання даних користувачів
    private record UserData(String username, String password, String role) implements Serializable {}
}


