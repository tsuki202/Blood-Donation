package org.example;

import java.sql.*;
import java.util.Optional;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/users";
    private static final String USER = "root";
    private static final String PASSWORD = "admin12345"; // Заміни на свій пароль

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Створюємо базові таблиці, якщо вони не існують
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "login VARCHAR(255) UNIQUE NOT NULL," +
                    "username VARCHAR(255) NOT NULL," + // Додаємо поле username
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(50) NOT NULL" +
                    ")";

            String createDonorsTable = "CREATE TABLE IF NOT EXISTS donors (" +
                    "id INT PRIMARY KEY," +
                    "name VARCHAR(255)," +
                    "surname VARCHAR(255)," +
                    "year INT," +
                    "blood_type VARCHAR(10)," +
                    "weight INT," +
                    "height INT," +
                    "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

            String createRecipientsTable = "CREATE TABLE IF NOT EXISTS recipients (" +
                    "id INT PRIMARY KEY," +
                    "name VARCHAR(255)," +
                    "surname VARCHAR(255)," +
                    "year INT," +
                    "needed_blood_type VARCHAR(10)," +
                    "request_date DATE," +
                    "valid_until DATE," +
                    "medical_condition TEXT," +
                    "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

            String createDonorQuestionnairesTable = "CREATE TABLE IF NOT EXISTS donor_questionnaires (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "donor_id INT," +
                    "weight INT," +
                    "height INT," +
                    "feels_good VARCHAR(10)," +
                    "had_symptoms VARCHAR(10)," +
                    "medications TEXT," +
                    "chronic_diseases VARCHAR(10)," +
                    "allergies VARCHAR(10)," +
                    "recent_surgeries VARCHAR(10)," +
                    "tattoos VARCHAR(10)," +
                    "alcohol VARCHAR(10)," +
                    "FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE" +
                    ")";

            String createDonationsTable = "CREATE TABLE IF NOT EXISTS donations (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "donor_id INT," +
                    "donation_date DATE," +
                    "amount INT," +
                    "location VARCHAR(255)," +
                    "FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE" +
                    ")";

            String createDonationScheduleTable = "CREATE TABLE IF NOT EXISTS donation_schedule (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "donor_id INT," +
                    "scheduled_date DATE," +
                    "location VARCHAR(255)," +
                    "status VARCHAR(50)," +
                    "FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE" +
                    ")";

            String createRecipientRequestsTable = "CREATE TABLE IF NOT EXISTS recipient_requests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "recipient_id INT," +
                    "status VARCHAR(50)," +
                    "response_date DATE," +
                    "notes TEXT," +
                    "FOREIGN KEY (recipient_id) REFERENCES recipients(id) ON DELETE CASCADE" +
                    ")";

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createUsersTable);
                stmt.execute(createDonorsTable);
                stmt.execute(createRecipientsTable);
                stmt.execute(createDonorQuestionnairesTable);
                stmt.execute(createDonationsTable);
                stmt.execute(createDonationScheduleTable);
                stmt.execute(createRecipientRequestsTable);
                System.out.println("✅ База даних ініціалізована успішно.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Помилка при ініціалізації бази даних: " + e.getMessage());
        }
    }

    public static boolean registerUser(String login, String password, String role) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Починаємо транзакцію

            // Додаємо користувача в таблицю users
            // Змінюємо запит для включення поля username (встановлюємо таке ж значення як і login)
            String userQuery = "INSERT INTO users (login, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, login);
            userStmt.setString(2, login); // Використовуємо login як значення для username
            userStmt.setString(3, password);
            userStmt.setString(4, role);

            int userResult = userStmt.executeUpdate();
            if (userResult <= 0) {
                conn.rollback();
                return false;
            }

            // Отримуємо згенерований ID
            ResultSet rs = userStmt.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            int userId = rs.getInt(1);

            // Додаємо запис в таблицю відповідно до ролі
            switch (role.toUpperCase()) {
                case "DONOR" -> {
                    String donorQuery = "INSERT INTO donors (id, name, surname, year, blood_type, weight, height) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement donorStmt = conn.prepareStatement(donorQuery);

                    donorStmt.setInt(1, userId);
                    donorStmt.setString(2, "");  // Початкові значення для нових донорів
                    donorStmt.setString(3, "");
                    donorStmt.setInt(4, 0);
                    donorStmt.setString(5, "");
                    donorStmt.setInt(6, 0);
                    donorStmt.setInt(7, 0);

                    donorStmt.executeUpdate();
                }
                case "RECIPIENT" -> {
                    String recipientQuery = "INSERT INTO recipients (id, name, surname, year, needed_blood_type, " +
                            "request_date, valid_until, medical_condition) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement recipientStmt = conn.prepareStatement(recipientQuery);

                    recipientStmt.setInt(1, userId);
                    recipientStmt.setString(2, "");  // Початкові значення для нових реципієнтів
                    recipientStmt.setString(3, "");
                    recipientStmt.setInt(4, 0);
                    recipientStmt.setString(5, "");
                    recipientStmt.setDate(6, new Date(System.currentTimeMillis()));
                    recipientStmt.setDate(7, new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // +30 днів
                    recipientStmt.setString(8, "");

                    recipientStmt.executeUpdate();
                }
            }

            conn.commit(); // Завершуємо транзакцію успішно
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Відкат транзакції при помилці
                }
            } catch (SQLException ex) {
                System.out.println("❌ Помилка при відкаті транзакції: " + ex.getMessage());
            }
            System.out.println("❌ Помилка при реєстрації: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Помилка при закритті з'єднання: " + e.getMessage());
            }
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
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("🔹 ID: " + rs.getInt("id") + ", Логін: " + rs.getString("login"));
            }
            if (!found) {
                System.out.println("Користувачів з роллю " + role + " не знайдено.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Помилка при виводі користувачів: " + e.getMessage());
        }
    }

    public static boolean deleteUserByLoginAndRole(String login, String role) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // First get the user ID
            int userId = -1;
            PreparedStatement getUserIdStmt = conn.prepareStatement(
                    "SELECT id FROM users WHERE login = ? AND role = ?");
            getUserIdStmt.setString(1, login);
            getUserIdStmt.setString(2, role);
            ResultSet rs = getUserIdStmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id");
            } else {
                return false; // User not found
            }

            // Delete related records based on role
            if (role.equals("DONOR")) {
                // Delete from donation_schedule
                PreparedStatement deleteScheduleStmt = conn.prepareStatement(
                        "DELETE FROM donation_schedule WHERE donor_id = ?");
                deleteScheduleStmt.setInt(1, userId);
                deleteScheduleStmt.executeUpdate();

                // Delete from donations
                PreparedStatement deleteDonationsStmt = conn.prepareStatement(
                        "DELETE FROM donations WHERE donor_id = ?");
                deleteDonationsStmt.setInt(1, userId);
                deleteDonationsStmt.executeUpdate();

                // Delete from donor_questionnaires
                PreparedStatement deleteQuestionnairesStmt = conn.prepareStatement(
                        "DELETE FROM donor_questionnaires WHERE donor_id = ?");
                deleteQuestionnairesStmt.setInt(1, userId);
                deleteQuestionnairesStmt.executeUpdate();

                // Delete from donors
                PreparedStatement deleteDonorStmt = conn.prepareStatement(
                        "DELETE FROM donors WHERE id = ?");
                deleteDonorStmt.setInt(1, userId);
                deleteDonorStmt.executeUpdate();
            } else if (role.equals("RECIPIENT")) {
                // Delete from recipient_requests
                PreparedStatement deleteRequestsStmt = conn.prepareStatement(
                        "DELETE FROM recipient_requests WHERE recipient_id = ?");
                deleteRequestsStmt.setInt(1, userId);
                deleteRequestsStmt.executeUpdate();

                // Delete from recipients
                PreparedStatement deleteRecipientStmt = conn.prepareStatement(
                        "DELETE FROM recipients WHERE id = ?");
                deleteRecipientStmt.setInt(1, userId);
                deleteRecipientStmt.executeUpdate();
            }

            // Finally delete from users
            PreparedStatement deleteUserStmt = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?");
            deleteUserStmt.setInt(1, userId);
            int result = deleteUserStmt.executeUpdate();

            conn.commit(); // Commit transaction
            return result > 0;

        } catch (SQLException e) {
            System.out.println("❌ Помилка при видаленні користувача: " + e.getMessage());
            return false;
        }
    }

//    public static boolean updateDonorInfo(int id, int weight, int height) {
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(
//                     "UPDATE donors SET weight = ?, height = ? WHERE id = ?")) {
//            stmt.setInt(1, weight);
//            stmt.setInt(2, height);
//            stmt.setInt(3, id);
//            return stmt.executeUpdate() > 0;
//        } catch (Exception e) {
//            System.out.println("❌ Помилка при оновленні даних донора: " + e.getMessage());
//            return false;
//        }
//    }

    public static boolean createRecipientRequest(int recipientId, String notes) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO recipient_requests (recipient_id, status, response_date, notes) " +
                            "VALUES (?, 'pending', NULL, ?)");
            stmt.setInt(1, recipientId);
            stmt.setString(2, notes);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Помилка при створенні запиту: " + e.getMessage());
            return false;
        }
    }

//    public static boolean updateRequestStatus(int requestId, String status, String adminComment) {
//        try (Connection conn = getConnection()) {
//            PreparedStatement stmt = conn.prepareStatement(
//                    "UPDATE recipient_requests SET status = ?, notes = CONCAT(notes, '\n--\nВідповідь адміна: ', ?), " +
//                            "response_date = CURRENT_DATE WHERE id = ?");
//            stmt.setString(1, status);
//            stmt.setString(2, adminComment);
//            stmt.setInt(3, requestId);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.out.println("❌ Помилка при оновленні статусу запиту: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static void printPendingRequests() {
//        try (Connection conn = getConnection()) {
//            PreparedStatement stmt = conn.prepareStatement(
//                    "SELECT rr.id, r.id AS recipient_id, u.login, r.name, r.surname, " +
//                            "r.needed_blood_type, rr.notes, rr.status " +
//                            "FROM recipient_requests rr " +
//                            "JOIN recipients r ON rr.recipient_id = r.id " +
//                            "JOIN users u ON r.id = u.id " +
//                            "WHERE rr.status = 'pending' " +
//                            "ORDER BY rr.id DESC");
//
//            ResultSet rs = stmt.executeQuery();
//            boolean hasRequests = false;
//
//            System.out.println("\n📋 Нові запити від реципієнтів:");
//
//            while (rs.next()) {
//                hasRequests = true;
//                int requestId = rs.getInt("id");
//                String login = rs.getString("login");
//                String name = rs.getString("name");
//                String surname = rs.getString("surname");
//                String bloodType = rs.getString("needed_blood_type");
//                String notes = rs.getString("notes");
//
//                System.out.println("\n🔹 Запит #" + requestId);
//                System.out.println("Логін: " + login);
//                System.out.println("Ім'я: " + name + " " + surname);
//                System.out.println("Необхідна група крові: " + bloodType);
//                System.out.println("Запит: " + notes);
//            }
//
//            if (!hasRequests) {
//                System.out.println("Немає нових запитів.");
//            }
//
//        } catch (SQLException e) {
//            System.out.println("❌ Помилка при отриманні запитів: " + e.getMessage());
//        }
//    }
}