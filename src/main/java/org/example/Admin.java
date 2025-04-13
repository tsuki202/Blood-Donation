package org.example;

import java.util.Scanner;

@Role("ADMIN")
public class Admin extends User {
    public Admin(int id, String username) {
        super(id, username, "ADMIN");
    }

    @Override
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\uD83D\uDCCC Ви зайшли як АДМІН.");
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
                case 1 -> selectUserRoleForAction("view");
                case 2 -> selectUserRoleForAction("delete");
                case 3 -> System.out.println("\uD83D\uDCCC Вихід з режиму адміністратора.");
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 3);
    }

    private void selectUserRoleForAction(String action) {
        Scanner scanner = new Scanner(System.in);
        int subChoice;
        do {
            System.out.println("Оберіть роль користувача:");
            System.out.println("1 - Адміністратори");
            System.out.println("2 - Донори");
            System.out.println("3 - Реципієнти");
            System.out.println("4 - Вихід");
            System.out.print("Ваш вибір: ");

            while (!scanner.hasNextInt()) {
                System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
                scanner.next();
            }
            subChoice = scanner.nextInt();
            scanner.nextLine();

            String selectedRole = switch (subChoice) {
                case 1 -> "ADMIN";
                case 2 -> "DONOR";
                case 3 -> "RECIPIENT";
                default -> null;
            };

            if (selectedRole != null) {
                if (action.equals("view")) {
                    DatabaseManager.deleteUserByLoginAndRole(selectedRole);
                } else if (action.equals("delete")) {
                    System.out.print("Введіть логін користувача для видалення: ");
                    String loginToDelete = scanner.nextLine();
                    DatabaseManager.deleteUserByLoginAndRole(loginToDelete);
                }
            }
        } while (subChoice != 4);
    }
}
