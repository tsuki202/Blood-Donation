package org.example;

import java.util.Scanner;

public class Admin {
    private final int id;
    private final String username;
    private final Scanner scanner = new Scanner(System.in);

    public Admin(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n\uD83D\uDCBC Адмін-меню:");
            System.out.println("1 - Переглянути користувачів за ролями");
            System.out.println("2 - Видалити користувача");
            System.out.println("3 - Вийти в головне меню");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> displayUsersByRole();
                case "2" -> deleteUserByRole();
                case "3" -> {
                    return;
                }
                default -> System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private void displayUsersByRole() {
        while (true) {
            System.out.println("\n\uD83D\uDCCB Оберіть роль для перегляду:");
            System.out.println("1 - Адміни");
            System.out.println("2 - Донори");
            System.out.println("3 - Реципієнти");
            System.out.println("4 - Назад");
            System.out.print("Ваш вибір: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> DatabaseManager.printUsersByRole("ADMIN");
                case "2" -> DatabaseManager.printUsersByRole("DONOR");
                case "3" -> DatabaseManager.printUsersByRole("RECIPIENT");
                case "4" -> {
                    return;
                }
                default -> System.out.println("❌ Невірний вибір.");
            }
        }
    }

    private void deleteUserByRole() {
        while (true) {
            System.out.println("\n\uD83D\uDDD1️ Оберіть роль користувача для видалення:");
            System.out.println("1 - Адміни");
            System.out.println("2 - Донори");
            System.out.println("3 - Реципієнти");
            System.out.println("4 - Назад");
            System.out.print("Ваш вибір: ");

            String input = scanner.nextLine();
            String role = null;

            if (input.equals("1")) {
                role = "ADMIN";
            } else if (input.equals("2")) {
                role = "DONOR";
            } else if (input.equals("3")) {
                role = "RECIPIENT";
            } else if (input.equals("4")) {
                break; // вихід із методу
            } else {
                System.out.println("❌ Невірний вибір.");
                continue; // повертаємось до початку циклу
            }

            // Виводимо список користувачів з обраною роллю
            DatabaseManager.printUsersByRole(role);

            System.out.print("Введіть логін користувача для видалення: ");
            String login = scanner.nextLine();

            boolean success = DatabaseManager.deleteUserByLoginAndRole(login, role);
            if (success) {
                System.out.println("✅ Користувача видалено.");
                DatabaseManager.printUsersByRole(role); // Показати оновлений список
            } else {
                System.out.println("❌ Користувача з таким логіном не знайдено або не відповідає ролі.");
            }
        }
    }
}
