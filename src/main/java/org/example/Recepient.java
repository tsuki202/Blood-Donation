package org.example;



import java.util.Scanner;

@Role("РЕЦИПІЄНТ")
class Recipient extends User {
    public Recipient(String username, String role, Registration registration) {
        super(username,role, registration);
    }

    @Override
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("📌 Ви зайшли як РЕЦИПІЄНТ.");
            System.out.println("1 - Запросити кров");
            System.out.println("2 - Переглянути історію запитів");
            System.out.println("3 - Вийти");

            while (!scanner.hasNextInt()) {
                System.out.println("❌ Невірний вибір! Введіть число.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> System.out.println("🩸 Введіть необхідну групу крові...");
                case 2 -> System.out.println("📜 Історія запитів...");
                case 3 -> exit();
                default -> System.out.println("❌ Невірний вибір!");
            }
        } while (choice != 3);
    }
}
