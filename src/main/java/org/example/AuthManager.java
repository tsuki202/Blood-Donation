package org.example;

import java.io.*;
import java.util.*;

class AuthManager {
    private Registration registration;
    private Scanner scanner;

    public AuthManager() {
        this.registration = new Registration();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            showMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> loginUser();
                case 2 -> registerUser();
                case 3 -> exitProgram();
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        }
    }

    private void exitProgram() {
        System.out.println("Вихід з програми. До побачення!");
        System.exit(0);
    }

    private void showMenu() {
        System.out.println("\n📌 Оберіть дію:");
        System.out.println("1️⃣ - Вхід");
        System.out.println("2️⃣ - Реєстрація");
        System.out.println("3️⃣ - Вихід");
    }

    private int getUserChoice() {
        System.out.print("🔹 Ваш вибір: ");
        while (!scanner.hasNextInt()) {
            System.out.println("⚠️ Будь ласка, введіть число!");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Очищення буфера
        return choice;
    }

    private void loginUser() {
        System.out.print("\n👤 Введіть логін: ");
        String username = scanner.nextLine().trim();
        System.out.print("🔑 Введіть пароль: ");
        String password = scanner.nextLine();

        if (registration.userExists(username) && registration.getPassword(username).equals(password)) {
            String role = registration.getRole(username);
            if ("ADMIN".equals(role)) {
                new Admin(username, registration).showMenu();
            } else if ("ДОНОР".equals(role)) {
                new Donor(username, registration).showMenu();
            } else {
                new Recipient(username, registration).showMenu();
            }
        } else {
            System.out.println("❌ Невірний логін або пароль.");
        }
    }

    private void registerUser() {
        System.out.print("\n🆕 Введіть новий логін: ");
        String username = scanner.nextLine().trim();

        if (registration.userExists(username)) {
            System.out.println("❌ Логін вже зайнятий. Спробуйте інший.");
        } else {
            System.out.print("🔑 Введіть пароль: ");
            String password = scanner.nextLine();

            System.out.print("🔑 Введіть роль (ADMIN/ДОНОР/РЕЦИПІЄНТ): ");
            String role = scanner.nextLine().trim().toUpperCase();

            if (!role.equals("ADMIN") && !role.equals("ДОНОР") && !role.equals("РЕЦИПІЄНТ")) {
                System.out.println("❌ Невірна роль. За замовчуванням буде встановлена роль РЕЦИПІЄНТ.");
                role = "РЕЦИПІЄНТ";
            }

            registration.register(username, password, role);
        }
    }
}