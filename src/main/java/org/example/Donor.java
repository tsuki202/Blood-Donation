package org.example;

import java.util.Scanner;

class Donor extends User {
    public Donor(String username, Registration registration) {
        super(username, "Донор", registration);
    }

    @Override
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n📌 Ви зайшли як ДОНОР.");
            System.out.println("1 - Пройти анкету та запланувати здачу крові");
            System.out.println("2 - Переглянути історію здачі");
            System.out.println("3 - Вийти");
            System.out.print("Оберіть дію: ");

            while (!scanner.hasNextInt()) {
                System.out.println("❌ Невірний вибір! Введіть число.");
                scanner.next();
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    boolean canDonate = donorSurvey(scanner);
                    if (canDonate) {
                        System.out.println("\n✅ Ви можете стати донором!");
                        System.out.print("📅 Бажаєте запланувати здачу крові? (так/ні): ");
                        String schedule = scanner.next().trim().toLowerCase();
                        if (schedule.equals("так")) {
                            System.out.println("📅 Здачу крові заплановано!");
                        } else {
                            System.out.println("❌ Планування скасовано.");
                        }
                    } else {
                        System.out.println("\n❌ Ви не можете бути донором. Рекомендуємо звернутися до лікаря.");
                    }
                }
                case 2 -> System.out.println("📜 Історія здачі крові...");
                case 3 -> System.out.println("📌 Ви вийшли.");
                default -> System.out.println("❌ Невірний вибір!");
            }
        } while (choice != 3);
    }

    private boolean donorSurvey(Scanner scanner) {
        System.out.println("\n📋 Опитування для донорства крові:");

        int age = getValidIntInput(scanner, "👤 Ваш вік (18-60 років): ");
        if (age < 18 || age > 60) {
            System.out.println("❌ Вік не підходить для донорства.");
            return false;
        }

        int weight = getValidIntInput(scanner, "⚖ Ваша вага (не менше 50 кг): ");
        if (weight < 50) {
            System.out.println("❌ Вага занадто мала для безпечного донорства.");
            return false;
        }

        if (askYes(scanner, "💊 Чи приймаєте ви ліки?")) {
            System.out.println("❌ Прийом ліків може впливати на якість крові.");
            return false;
        }

        if (askYes(scanner, "🦠 Чи хворіли ви на інфекційні хвороби за останні 6 місяців?")) {
            System.out.println("❌ Інфекційні хвороби є протипоказанням.");
            return false;
        }

        if (askYes(scanner, "💉 Чи робили ви вакцинацію за останні 30 днів?")) {
            System.out.println("❌ Вакцинація впливає на склад крові.");
            return false;
        }

        if (askYes(scanner, "🩸 Чи здавали ви кров за останні 2 місяці?")) {
            System.out.println("❌ Недостатньо часу пройшло з останньої здачі.");
            return false;
        }

        return true;
    }

    private int getValidIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("❌ Введіть коректне число.");
            scanner.next();
            System.out.print(prompt);
        }
        return scanner.nextInt();
    }

    private boolean askYes(Scanner scanner, String question) {
        System.out.print(question + " (так/ні): ");
        String response = scanner.next().trim().toLowerCase();
        return response.equals("так");
    }
}