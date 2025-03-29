package org.example;

import java.io.*;
import java.util.*;

public class Registration {
    private static final String FILE_NAME = "users.txt";
    private HashMap<String, User> users = new HashMap<>();

    public String getUserRole(String username) {

        return username;
    }

    public void registerUser(String username, String password, String role) {
    }

    // Клас User для збереження паролю та ролі
    public static class User {
        private String password;
        private String role;

        public User(String password, String role) {
            this.password = password;
            this.role = role;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }
    }

    public Registration() {
        loadUsers();
    }

    public void register(String username, String password, String role) {
        if (users.containsKey(username)) {
            System.out.println("Користувач з таким логіном вже існує!");
        } else {
            users.put(username, new User(password, role));
            saveUsers();
            System.out.println("Реєстрація успішна! Тепер ви можете увійти.");
        }
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public String getPassword(String username) {
        return users.get(username).getPassword();
    }

    public String getRole(String username) {
        return users.get(username).getRole();
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (var entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue().getPassword() + ":" + entry.getValue().getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Помилка збереження користувачів: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    users.put(parts[0], new User(parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Помилка завантаження користувачів: " + e.getMessage());
        }
    }

    public void listUsers() {
        for (String username : users.keySet()) {
            System.out.println("Користувач: " + username + ", Роль: " + users.get(username).getRole());
        }
    }

    public void deleteUser(String usernameToDelete) {
        if (users.containsKey(usernameToDelete)) {
            users.remove(usernameToDelete);
            saveUsers();
            System.out.println("Користувач " + usernameToDelete + " був видалений.");
        } else {
            System.out.println("Користувача з таким логіном не існує.");
        }
    }
}