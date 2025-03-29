package org.example;

import java.util.Scanner;

public class Admin extends User {

    public Admin(String username, Registration registration) {
        super(username, "ADMIN", registration);
    }

    @Override
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("📌 Ви зайшли як АДМІН.");
            System.out.println("1 - Переглянути список користувачів");
            System.out.println("2 - Видалити користувача");
            System.out.println("3 - Вийти");
            System.out.print("Оберіть дію: ");

            while (!scanner.hasNextInt()) { // Перевірка на коректний ввід
                System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
                scanner.next();
            }

            choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            switch (choice) {
                case 1 -> registration.listUsers();
                case 2 -> deleteUser();
                case 3 -> System.out.println("📌 Вихід з режиму адміністратора.");
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 3);
    }

    private void deleteUser() {
        System.out.print("Введіть логін користувача для видалення: ");
        Scanner scanner = new Scanner(System.in);
        String usernameToDelete = scanner.nextLine();
        registration.deleteUser(usernameToDelete);
    }
}