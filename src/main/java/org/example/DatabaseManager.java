package org.example;

import java.sql.*;
import java.sql.Date;
import java.util.*;
class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/users";
    private static final String USER = "root";
    private static final String PASSWORD = "admin12345";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void registerUser(String login, String password, String role) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (login, password, role) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                Scanner scanner = new Scanner(System.in);
                switch (role.toUpperCase()) {
                    case "ADMIN" -> {
                        System.out.print("Введіть ім'я адміністратора: ");
                        String name = scanner.nextLine();
                        PreparedStatement ps = conn.prepareStatement("INSERT INTO admins (id, name) VALUES (?, ?)");
                        ps.setInt(1, userId);
                        ps.setString(2, name);
                        ps.executeUpdate();
                    }
                    case "DONOR" -> {
                        System.out.print("Ім'я: ");
                        String name = scanner.nextLine();
                        System.out.print("Прізвище: ");
                        String surname = scanner.nextLine();
                        System.out.print("Рік народження: ");
                        int year = Integer.parseInt(scanner.nextLine());
                        System.out.print("Група крові: ");
                        String blood = scanner.nextLine();
                        PreparedStatement ps = conn.prepareStatement("INSERT INTO donors (id, name, surname, year, blood_type) VALUES (?, ?, ?, ?, ?)");
                        ps.setInt(1, userId);
                        ps.setString(2, name);
                        ps.setString(3, surname);
                        ps.setInt(4, year);
                        ps.setString(5, blood);
                        ps.executeUpdate();
                    }
                    case "RECIPIENT" -> {
                        System.out.print("Ім'я: ");
                        String name = scanner.nextLine();
                        System.out.print("Прізвище: ");
                        String surname = scanner.nextLine();
                        System.out.print("Рік народження: ");
                        int year = Integer.parseInt(scanner.nextLine());
                        System.out.print("Яку групу крові потребує: ");
                        String neededBlood = scanner.nextLine();
                        System.out.print("Дата запиту (yyyy-mm-dd): ");
                        String requestDate = scanner.nextLine();
                        System.out.print("Дійсний до (yyyy-mm-dd): ");
                        String untilDate = scanner.nextLine();
                        PreparedStatement ps = conn.prepareStatement("INSERT INTO recipients (id, name, surname, year, needed_blood_type, request_date, valid_until) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, userId);
                        ps.setString(2, name);
                        ps.setString(3, surname);
                        ps.setInt(4, year);
                        ps.setString(5, neededBlood);
                        ps.setDate(6, Date.valueOf(requestDate));
                        ps.setDate(7, Date.valueOf(untilDate));
                        ps.executeUpdate();
                    }
                }
                System.out.println("✅ Користувач успішно зареєстрований.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при реєстрації: " + e.getMessage());
        }
    }

    public static Optional<UserData> getUserByLogin(String login) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE login = ?");
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new UserData(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка пошуку користувача: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static void listAllUsers() {
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT id, login, role FROM users");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Логін: " + rs.getString("login") + ", Роль: " + rs.getString("role"));
            }
        } catch (Exception e) {
            System.out.println("❌ Не вдалося отримати список: " + e.getMessage());
        }
    }

    public static void listUsersByRole(String role) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, login FROM users WHERE role = ?");
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Логін: " + rs.getString("login") + ", Роль: " + role);
            }
        } catch (Exception e) {
            System.out.println("❌ Не вдалося отримати список користувачів за роллю: " + e.getMessage());
        }
    }

    public void deleteUserByRole() {
        while (true) {
            System.out.println("\n🗑️ Оберіть роль користувача для видалення:");
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
                    System.out.print("Введіть ID користувача для видалення: ");
                    String idInput = scanner.nextLine();
                    if (!idInput.matches("\\d+")) {
                        System.out.println("❌ Невірний ID. Потрібно ввести число.");
                        continue;
                    }
                    int id = Integer.parseInt(idInput);
                    DatabaseManager.deleteUserById(id);
                    System.out.println("📋 Оновлений список користувачів:");
                    DatabaseManager.printUsersByRole(role);
                }
                case "4" -> {
                    return;
                }
                default -> System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

}