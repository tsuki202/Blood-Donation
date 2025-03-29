package org.example;

import java.util.Scanner;

class Recipient extends User {
    public Recipient(String username, Registration registration) {
        super(username, "Рецепієнт", registration);
    }

    @Override
    public void showMenu() {
        System.out.println("📌 Ви зайшли як РЕЦИПІЄНТ.");
        System.out.println("1 - Запросити кров");
        System.out.println("2 - Переглянути історію запитів");
        System.out.println("3 - Вийти");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> System.out.println("🩸 Введіть необхідну групу крові...");
            case 2 -> System.out.println("📜 Історія запитів...");
            case 3 -> System.out.println("📌 Ви вийшли.");
            default -> System.out.println("❌ Невірний вибір!");
        }
    }
}