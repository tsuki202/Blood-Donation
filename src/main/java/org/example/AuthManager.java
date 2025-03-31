package org.example;

import java.util.*;

public class AuthManager {
    private final Scanner scanner;
    private final Registration registration;

    public AuthManager() {
        this.scanner = new Scanner(System.in);
        this.registration = new Registration();
    }

    public void start() {
        while (true) {
            System.out.println("\n🔹 Вітаємо у системі донорства крові!");
            System.out.println("1 - Реєстрація");
            System.out.println("2 - Вхід");
            System.out.println("3 - Вийти");

            System.out.print("Оберіть опцію: ");
            int choice = getIntInput();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> {
                    System.out.println("👋 Дякуємо за використання! До побачення.");
                    return;
                }
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        }
    }

    private void registerUser() {
        System.out.print("👤 Введіть логін: ");
        String username = scanner.nextLine();

        if (registration.userExists(username)) {
            System.out.println("⚠ Користувач із таким логіном вже існує!");
            return;
        }

        System.out.print("🔑 Введіть пароль: ");
        String password = scanner.nextLine();

        System.out.println("🆔 Оберіть роль:");
        System.out.println("1 - Донор");
        System.out.println("2 - Реципієнт");
        System.out.println("3 - Адмін");
        System.out.print("Ваш вибір: ");
        int roleChoice = getIntInput();

        String role = switch (roleChoice) {
            case 1 -> "ДОНОР";
            case 2 -> "РЕЦИПІЄНТ";
            case 3 -> "ADMIN";
            default -> {
                System.out.println("❌ Невірний вибір! Реєстрацію скасовано.");
                yield null;
            }
        };

        if (role != null) {
            registration.register(username, password, role);
        }
    }

    private void loginUser() {
        System.out.print("👤 Введіть логін: ");
        String username = scanner.nextLine();

        if (!registration.userExists(username)) {
            System.out.println("❌ Користувача не знайдено!");
            return;
        }

        System.out.print("🔑 Введіть пароль: ");
        String password = scanner.nextLine();

        if (!password.equals(registration.getPassword(username))) {
            System.out.println("❌ Неправильний пароль!");
            return;
        }

        String role = registration.getRole(username);
        User user = switch (role) {
            case "ДОНОР" -> new Donor(username, role,registration);
            case "РЕЦИПІЄНТ" -> new Recipient(username, role,registration);
            case "ADMIN" -> new Admin(username, role,registration);
            default -> null;
        };

        if (user != null) {
            user.showMenu();
        } else {
            System.out.println("❌ Помилка визначення ролі!");
        }
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("❌ Введіть число!");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // очищення буфера
        return input;
    }
}



