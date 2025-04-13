package org.example;

import java.util.Optional;
import java.util.Scanner;

class AuthManager {
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n\uD83D\uDCCC Оберіть дію:");
            System.out.println("1️⃣ - Вхід");
            System.out.println("2️⃣ - Реєстрація");
            System.out.println("3️⃣ - Вихід");
            System.out.print("🔹 Ваш вибір: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) {
                System.out.println("❌ Невірний ввід. Введіть число.");
                continue;
            }

            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1 -> loginUser();
                case 2 -> registerUser();
                case 3 -> exitProgram();
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        }
    }

    private void loginUser() {
        System.out.print("\n\uD83D\uDC64 Введіть логін: ");
        String login = scanner.nextLine().trim();
        System.out.print("\uD83D\uDD11 Введіть пароль: ");
        String password = scanner.nextLine();

        DatabaseManager.getUserByLogin(login).ifPresentOrElse(user -> {
            if (user.getPassword().equals(password)) {
                System.out.println("✅ Вхід успішний!");
                switch (user.getRole().toUpperCase()) {
                    case "ADMIN" -> new Admin(user.getId(), user.getUsername()).showMenu();
                    case "DONOR" -> Optional.ofNullable(Donor.fromDatabase(user.getId(), user.getUsername())).ifPresent(Donor::showMenu);
                    case "RECIPIENT" -> Optional.ofNullable(Recipient.fromDatabase(user.getId(), user.getUsername())).ifPresent(Recipient::showMenu);
                    default -> System.out.println("\uD83D\uDC64 Ви увійшли як " + user.getRole() + ". Немає додаткового меню.");
                }
            } else {
                System.out.println("❌ Невірний пароль.");
            }
        }, () -> System.out.println("❌ Користувач не знайдений."));
    }

    private void registerUser() {
        System.out.print("\n\uD83C\uDD95 Введіть логін: ");
        String login = scanner.nextLine().trim();
        System.out.print("\uD83D\uDD10 Введіть пароль: ");
        String password = scanner.nextLine();

        String role = null;
        while (role == null) {
            System.out.println("\uD83C\uDFBE Оберіть роль:");
            System.out.println("1 - Адмін");
            System.out.println("2 - Донор");
            System.out.println("3 - Реципієнт");
            System.out.print("Ваш вибір: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> role = "ADMIN";
                case "2" -> role = "DONOR";
                case "3" -> role = "RECIPIENT";
                default -> System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
            }
        }

        boolean success = DatabaseManager.registerUser(login, password, role);
        if (success) {
            System.out.println("✅ Реєстрація успішна.");
        } else {
            System.out.println("❌ Помилка при реєстрації. Можливо, логін вже існує.");
        }
    }

    private void exitProgram() {
        System.out.println("\uD83D\uDC4B До побачення!");
        System.exit(0);
    }

    public void displayUsersByRole() {
        while (true) {
            System.out.println("\n\uD83D\uDCCB Оберіть роль для перегляду:");
            System.out.println("1 - Адміни");
            System.out.println("2 - Донори");
            System.out.println("3 - Реципієнти");
            System.out.println("4 - Вихід");
            System.out.print("Ваш вибір: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> DatabaseManager.printUsersByRole("ADMIN");
                case "2" -> DatabaseManager.printUsersByRole("DONOR");
                case "3" -> DatabaseManager.printUsersByRole("RECIPIENT");
                case "4" -> {
                    return;
                }
                default -> System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    public void deleteUserByRole() {
        while (true) {
            System.out.println("\n\uD83D\uDDD1️ Оберіть роль користувача для видалення:");
            System.out.println("1 - Адміни");
            System.out.println("2 - Донори");
            System.out.println("3 - Реципієнти");
            System.out.println("4 - Вихід");
            System.out.print("Ваш вибір: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1", "2", "3" -> {
                    String role = switch (input) {
                        case "1" -> "ADMIN";
                        case "2" -> "DONOR";
                        case "3" -> "RECIPIENT";
                        default -> throw new IllegalStateException("Unexpected value: " + input);
                    };
                    DatabaseManager.printUsersByRole(role);
                    System.out.print("Введіть логін користувача для видалення: ");
                    String login = scanner.nextLine();
                    boolean success = DatabaseManager.deleteUserByLoginAndRole(login, role);
                    if (success) {
                        System.out.println("✅ Користувача видалено.");
                        DatabaseManager.printUsersByRole(role);
                    } else {
                        System.out.println("❌ Користувача з таким логіном не знайдено або не відповідає ролі.");
                    }
                }
                case "4" -> {
                    return;
                }
                default -> System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
            }
        }
    }
}






