package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
    private final int id;
    private final String login;
    private final Scanner scanner = new Scanner(System.in);

    public Admin(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n\uD83D\uDCBC Адмін-меню:");
            System.out.println("1 - Переглянути користувачів за ролями");
            System.out.println("2 - Видалити користувача");
            System.out.println("3 - Запити від реципієнтів");
            System.out.println("4 - Вийти в головне меню");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> displayUsersByRole();
                case "2" -> deleteUserByRole();
                case "3" -> manageRecipientRequests();
                case "4" -> {
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

    private void manageRecipientRequests() {
        try (Connection conn = DatabaseManager.getConnection()) {
            while (true) {
                System.out.println("\n\uD83D\uDCE9 Запити від реципієнтів:");
                System.out.println("1 - Переглянути нові запити");
                System.out.println("2 - Переглянути всі запити");
                System.out.println("3 - Відповісти на запит");
                System.out.println("4 - Назад");
                System.out.print("Ваш вибір: ");

                String input = scanner.nextLine();

                switch (input) {
                    case "1" -> viewRecipientRequests("pending");
                    case "2" -> viewRecipientRequests("all");
                    case "3" -> respondToRequest();
                    case "4" -> {
                        return;
                    }
                    default -> System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Помилка при підключенні до бази даних: " + e.getMessage());
        }
    }

    private void viewRecipientRequests(String filter) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query;
            PreparedStatement stmt;

            if (filter.equals("pending")) {
                query = "SELECT rr.id, r.id AS recipient_id, u.login, r.name, r.surname, " +
                        "r.needed_blood_type, rr.notes, rr.status, rr.response_date " +
                        "FROM recipient_requests rr " +
                        "JOIN recipients r ON rr.recipient_id = r.id " +
                        "JOIN users u ON r.id = u.id " +
                        "WHERE rr.status = 'pending' " +
                        "ORDER BY rr.id DESC";
                stmt = conn.prepareStatement(query);
            } else {
                query = "SELECT rr.id, r.id AS recipient_id, u.login, r.name, r.surname, " +
                        "r.needed_blood_type, rr.notes, rr.status, rr.response_date " +
                        "FROM recipient_requests rr " +
                        "JOIN recipients r ON rr.recipient_id = r.id " +
                        "JOIN users u ON r.id = u.id " +
                        "ORDER BY rr.status = 'pending' DESC, rr.id DESC";
                stmt = conn.prepareStatement(query);
            }

            ResultSet rs = stmt.executeQuery();
            boolean hasRequests = false;

            System.out.println("\n📋 " + (filter.equals("pending") ? "Нові" : "Всі") + " запити від реципієнтів:");

            while (rs.next()) {
                hasRequests = true;
                int requestId = rs.getInt("id");
                String login = rs.getString("login");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String bloodType = rs.getString("needed_blood_type");
                String status = rs.getString("status");
                String notes = rs.getString("notes");

                System.out.println("\n🔹 Запит #" + requestId);
                System.out.println("Логін: " + login);
                System.out.println("Ім'я: " + name + " " + surname);
                System.out.println("Необхідна група крові: " + bloodType);
                System.out.println("Статус: " + status);
                System.out.println("Запит: " + notes);
            }

            if (!hasRequests) {
                System.out.println("Немає " + (filter.equals("pending") ? "нових" : "") + " запитів.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Помилка при отриманні запитів: " + e.getMessage());
        }
    }

    private void respondToRequest() {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Спочатку виводимо список запитів зі статусом "pending"
            viewRecipientRequests("pending");

            System.out.print("\nВведіть номер запиту для відповіді (або 0 для виходу): ");
            String input = scanner.nextLine();

            if (input.equals("0")) {
                return;
            }

            try {
                int requestId = Integer.parseInt(input);

                // Перевіряємо, чи існує запит з таким ID
                PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT id FROM recipient_requests WHERE id = ?");
                checkStmt.setInt(1, requestId);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    System.out.println("Оберіть статус відповіді:");
                    System.out.println("1 - Схвалено");
                    System.out.println("2 - Відхилено");
                    System.out.println("3 - В обробці");
                    System.out.print("Ваш вибір: ");

                    String statusChoice = scanner.nextLine();
                    String status;

                    switch (statusChoice) {
                        case "1" -> status = "approved";
                        case "2" -> status = "rejected";
                        case "3" -> status = "processing";
                        default -> {
                            System.out.println("❌ Невірний вибір статусу.");
                            return;
                        }
                    }

                    System.out.print("Введіть коментар до відповіді: ");
                    String comment = scanner.nextLine();

                    // Оновлюємо запис у базі даних
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE recipient_requests SET status = ?, notes = CONCAT(notes, '\n--\nВідповідь адміна: ', ?), " +
                                    "response_date = CURRENT_DATE WHERE id = ?");
                    updateStmt.setString(1, status);
                    updateStmt.setString(2, comment);
                    updateStmt.setInt(3, requestId);

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("✅ Відповідь успішно збережена.");
                    } else {
                        System.out.println("❌ Помилка при збереженні відповіді.");
                    }
                } else {
                    System.out.println("❌ Запит з номером " + requestId + " не знайдено.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Невірний формат номера запиту.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Помилка при відповіді на запит: " + e.getMessage());
        }
    }
}