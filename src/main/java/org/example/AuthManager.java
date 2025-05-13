package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    case "ADMIN" -> new Admin(user.getId(), user.getLogin()).showMenu();
                    case "DONOR" -> {
                        Donor donor = Donor.fromDatabase(user.getId(), user.getLogin());
                        if (donor == null) {
                            // Якщо немає запису в таблиці donors, створюємо базовий запис
                            createInitialDonorRecord(user.getId(), user.getLogin());
                            donor = Donor.fromDatabase(user.getId(), user.getLogin());
                        }
                        if (donor != null) {
                            donor.showMenu();
                        } else {
                            System.out.println("❌ Помилка завантаження профілю донора.");
                        }
                    }
                    case "RECIPIENT" -> {
                        Recipient recipient = Recipient.fromDatabase(user.getId(), user.getLogin());
                        if (recipient == null) {
                            // Якщо немає запису в таблиці recipients, створюємо базовий запис
                            createInitialRecipientRecord(user.getId(), user.getLogin());
                            recipient = Recipient.fromDatabase(user.getId(), user.getLogin());
                        }
                        if (recipient != null) {
                            recipient.showMenu();
                        } else {
                            System.out.println("❌ Помилка завантаження профілю реципієнта.");
                        }
                    }
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

            // Після успішної реєстрації, отримуємо ID користувача
            String finalRole = role;
            DatabaseManager.getUserByLogin(login).ifPresent(user -> {
                int userId = user.getId();

                // Створюємо додаткові записи в залежності від ролі
                if (finalRole.equals("DONOR")) {
                    createInitialDonorRecord(userId, login);
                } else if (finalRole.equals("RECIPIENT")) {
                    createInitialRecipientRecord(userId, login);
                }
            });
        } else {
            System.out.println("❌ Помилка при реєстрації. Можливо, логін вже існує.");
        }
    }

    private void createInitialDonorRecord(int userId, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Перевіряємо, чи вже існує запис - исправлено!
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM donors WHERE id = ?");
            checkStmt.setInt(1, userId);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                return; // Запис вже існує
            }

            // Створюємо базовий запис донора
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO donors (id, name, surname, year, blood_type, weight, height) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, "Новий"); // Тимчасове ім'я
            stmt.setString(3, "Донор"); // Тимчасове прізвище
            stmt.setInt(4, 2000); // Тимчасовий рік народження
            stmt.setString(5, "Невідомо"); // Тимчасова група крові
            stmt.setInt(6, 70); // Тимчасова вага
            stmt.setInt(7, 170); // Тимчасовий зріст

            stmt.executeUpdate();
            System.out.println("✅ Створено початковий профіль донора.");
        } catch (SQLException e) {
            System.out.println("❌ Помилка при створенні профілю донора: " + e.getMessage());
        }
    }

    private void createInitialRecipientRecord(int userId, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Перевіряємо, чи вже існує запис - исправлено!
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM recipients WHERE id = ?");
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return; // Запис вже існує
            }

            // Створюємо поточну дату
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            // Створюємо дату, що на 30 днів пізніше від поточної
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
            java.sql.Date validUntilDate = new java.sql.Date(calendar.getTimeInMillis());

            // Створюємо базовий запис реципієнта
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO recipients (id, name, surname, year, needed_blood_type, request_date, valid_until, medical_condition) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, "Новий"); // Тимчасове ім'я
            stmt.setString(3, "Реципієнт"); // Тимчасове прізвище
            stmt.setInt(4, 2000); // Тимчасовий рік народження
            stmt.setString(5, "Невідомо"); // Тимчасова група крові
            stmt.setDate(6, currentDate); // Поточна дата
            stmt.setDate(7, validUntilDate); // Дата через 30 днів
            stmt.setString(8, "Не вказано"); // Тимчасовий медичний стан

            stmt.executeUpdate();
            System.out.println("✅ Створено початковий профіль реципієнта.");
        } catch (SQLException e) {
            System.out.println("❌ Помилка при створенні профілю реципієнта: " + e.getMessage());
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