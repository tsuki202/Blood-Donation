package org.example;

import java.util.Scanner;

@Role("ADMIN")
public class Admin extends User {
    public Admin(String username, String role, Registration registration) {
        super(username,role, registration);
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

            while (!scanner.hasNextInt()) {
                System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registration.listUsers();
                case 2 -> deleteUser(scanner);
                case 3 -> exit();
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 3);
    }

    private void deleteUser(Scanner scanner) {
        System.out.print("Введіть логін користувача для видалення: ");
        String usernameToDelete = scanner.nextLine();
        registration.deleteUser(usernameToDelete);
    }
}
