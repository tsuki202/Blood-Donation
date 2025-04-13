package org.example;

import java.sql.*;
import java.util.Optional;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/authdb";
    private static final String USER = "root";
    private static final String PASSWORD = "admin12345"; // Заміни на свій пароль

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean registerUser(String login, String password, String role) {
        String query = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, role);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Помилка при реєстрації: " + e.getMessage());
            return false;
        }
    }

    public static Optional<UserData> getUserByLogin(String login) {
        String query = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
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
        } catch (SQLException e) {
            System.out.println("❌ Помилка при пошуку користувача: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static void printUsersByRole(String role) {
        String query = "SELECT id, login FROM users WHERE role = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n🔎 Користувачі з роллю " + role + ":");
            while (rs.next()) {
                System.out.println("🔹 ID: " + rs.getInt("id") + ", Логін: " + rs.getString("login"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Помилка при виводі користувачів: " + e.getMessage());
        }
    }

    public static boolean deleteUserByLoginAndRole(String login, String role) {
        String query = "DELETE FROM users WHERE login = ? AND role = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, role);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Помилка при видаленні користувача: " + e.getMessage());
            return false;
        }
    }
}

