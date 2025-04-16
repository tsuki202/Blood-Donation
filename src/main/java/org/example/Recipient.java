package org.example;

import java.sql.*;
import java.util.Scanner;

@Role("RECIPIENT")
public class Recipient extends User {
    private String name;
    private String surname;
    private int year;
    private String neededBloodType;
    private Date requestDate;
    private Date validUntil;
    private String medicalCondition;

    public Recipient(int id, String username, String name, String surname, int year,
                     String neededBloodType, Date requestDate, Date validUntil, String medicalCondition) {
        super(id, username, "RECIPIENT");
        this.name = name;
        this.surname = surname;
        this.year = year;
        this.neededBloodType = neededBloodType;
        this.requestDate = requestDate;
        this.validUntil = validUntil;
        this.medicalCondition = medicalCondition;
    }

    @Override
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n🏥 Ви зайшли як Реципієнт " + username);
            if (name != null && !name.isEmpty()) {
                System.out.println("Ім'я: " + name + " " + surname);
            } else {
                System.out.println("⚠️ Ваш профіль не заповнений. Рекомендуємо заповнити дані.");
            }
            System.out.println("1 - Переглянути мої дані");
            System.out.println("2 - Заповнити/оновити персональні дані");
            System.out.println("3 - Пошук донорів за групою крові");
            System.out.println("4 - Перевірити статус запиту");
            System.out.println("5 - Оновити медичну інформацію");
            System.out.println("6 - Вийти");
            System.out.print("Оберіть дію: ");

            while (!scanner.hasNextInt()) {
                System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
                scanner.next();
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMyData();
                case 2 -> updatePersonalData(scanner);
                case 3 -> searchDonorsByBloodType();
                case 4 -> checkRequestStatus();
                case 5 -> updateMedicalInfo();
                case 6 -> System.out.println("🏥 Вихід з меню реципієнта.");
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 6);
    }

    private void updatePersonalData(Scanner scanner) {
        System.out.println("\n📝 Оновлення персональних даних");

        System.out.print("Введіть ваше ім'я: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            this.name = newName;
        }

        System.out.print("Введіть ваше прізвище: ");
        String newSurname = scanner.nextLine().trim();
        if (!newSurname.isEmpty()) {
            this.surname = newSurname;
        }

        System.out.print("Введіть рік народження: ");
        String yearStr = scanner.nextLine().trim();
        if (!yearStr.isEmpty() && yearStr.matches("\\d+")) {
            this.year = Integer.parseInt(yearStr);
        }

        System.out.print("Введіть необхідну групу крові (наприклад, A+, B-, O+): ");
        String newBloodType = scanner.nextLine().trim();
        if (!newBloodType.isEmpty()) {
            this.neededBloodType = newBloodType;
        }

        System.out.print("Введіть медичний стан: ");
        String newCondition = scanner.nextLine().trim();
        if (!newCondition.isEmpty()) {
            this.medicalCondition = newCondition;
        }

        // Зберігаємо дані в базу
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recipients SET name = ?, surname = ?, year = ?, needed_blood_type = ?, " +
                            "request_date = ?, valid_until = ?, medical_condition = ? WHERE id = ?");
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setInt(3, year);
            stmt.setString(4, neededBloodType);
            stmt.setDate(5, requestDate);
            stmt.setDate(6, validUntil);
            stmt.setString(7, medicalCondition);
            stmt.setInt(8, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("✅ Дані успішно оновлено!");
            } else {
                System.out.println("❌ Помилка при оновленні даних.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при оновленні даних: " + e.getMessage());
        }
    }

    private void viewMyData() {
        if (name == null || name.isEmpty()) {
            System.out.println("\n⚠️ Ваш профіль ще не заповнений. Оберіть пункт меню '2' для заповнення даних.");
            return;
        }

        System.out.println("\n📋 Ваші дані:");
        System.out.println("Ім'я: " + name);
        System.out.println("Прізвище: " + surname);
        System.out.println("Рік народження: " + year);
        System.out.println("Потрібна група крові: " + neededBloodType);
        System.out.println("Дата запиту: " + requestDate);
        System.out.println("Дійсний до: " + validUntil);
        System.out.println("Медичний стан: " + medicalCondition);
    }

    private void searchDonorsByBloodType() {
        if (neededBloodType == null || neededBloodType.isEmpty()) {
            System.out.println("\n⚠️ Спочатку вкажіть необхідну групу крові в своєму профілі.");
            return;
        }

        System.out.println("\n🔍 Пошук донорів з групою крові: " + neededBloodType);

        try (Connection conn = DatabaseManager.getConnection()) {
            // Спрощений запит, який шукає донорів лише за групою крові
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT d.id, d.name, d.surname, d.year, d.blood_type, d.weight, d.height " +
                            "FROM donors d WHERE d.blood_type = ?");

            stmt.setString(1, neededBloodType);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n🩸 Доступні донори:");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("\nID: " + rs.getInt("id"));
                System.out.println("Ім'я: " + rs.getString("name") + " " + rs.getString("surname"));
                int year = rs.getInt("year");
                if (year > 0) {
                    System.out.println("Вік: " + (java.time.Year.now().getValue() - year));
                }
                System.out.println("Група крові: " + rs.getString("blood_type"));
                System.out.println("Вага/Зріст: " + rs.getInt("weight") + " кг / " + rs.getInt("height") + " см");
            }

            if (!found) {
                System.out.println("На жаль, зараз немає доступних донорів з цією групою крові.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при пошуку донорів: " + e.getMessage());
        }
    }

    private void checkRequestStatus() {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT status, response_date, notes FROM recipient_requests WHERE recipient_id = ?");

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n📋 Статус вашого запиту:");
                System.out.println("Статус: " + rs.getString("status"));
                System.out.println("Дата відповіді: " + rs.getDate("response_date"));
                System.out.println("Примітки: " + rs.getString("notes"));
            } else {
                System.out.println("\nℹ️ У вас поки немає запитів або ваш запит ще обробляється.");

                // Пропонуємо створити запит, якщо його немає
                System.out.print("Бажаєте створити новий запит? (Так/Ні): ");
                Scanner scanner = new Scanner(System.in);
                String answer = scanner.nextLine();

                if (answer.equalsIgnoreCase("Так")) {
                    System.out.println("\n📝 Створення запиту реципієнта");
                    System.out.print("Опишіть ваш запит: ");
                    String notes = scanner.nextLine();

                    try {
                        PreparedStatement createStmt = conn.prepareStatement(
                                "INSERT INTO recipient_requests (recipient_id, status, response_date, notes) VALUES (?, 'pending', NULL, ?)");
                        createStmt.setInt(1, id);
                        createStmt.setString(2, notes);

                        if (createStmt.executeUpdate() > 0) {
                            System.out.println("✅ Запит успішно створено! Очікуйте на відповідь.");
                        } else {
                            System.out.println("❌ Помилка при створенні запиту.");
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Помилка при створенні запиту: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при перевірці статусу: " + e.getMessage());
            System.out.println("Перевірте, чи існує таблиця recipient_requests в базі даних.");
        }
    }

    private void updateMedicalInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n🏥 Оновлення медичної інформації");

        System.out.print("Поточна група крові (" + neededBloodType + "): ");
        String newBloodType = scanner.nextLine();
        if (!newBloodType.isEmpty()) {
            this.neededBloodType = newBloodType;
        }

        System.out.print("Медичний стан (" + medicalCondition + "): ");
        String newCondition = scanner.nextLine();
        if (!newCondition.isEmpty()) {
            this.medicalCondition = newCondition;
        }

        System.out.print("Дійсний до (yyyy-mm-dd) (" + validUntil + "): ");
        String newDate = scanner.nextLine();
        if (!newDate.isEmpty()) {
            try {
                this.validUntil = Date.valueOf(newDate);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Невірний формат дати. Використовуйте формат yyyy-mm-dd.");
                return;
            }
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recipients SET needed_blood_type = ?, medical_condition = ?, valid_until = ? WHERE id = ?");

            stmt.setString(1, neededBloodType);
            stmt.setString(2, medicalCondition);
            stmt.setDate(3, validUntil);
            stmt.setInt(4, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("\n✅ Медичну інформацію успішно оновлено!");
            } else {
                System.out.println("\n❌ Помилка при оновленні інформації.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при оновленні інформації: " + e.getMessage());
        }
    }

    public static Recipient fromDatabase(int id, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM recipients WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                int year = rs.getInt("year");
                String neededBloodType = rs.getString("needed_blood_type");
                Date requestDate = rs.getDate("request_date");
                Date validUntil = rs.getDate("valid_until");
                String medicalCondition = rs.getString("medical_condition");

                return new Recipient(id, username, name, surname, year, neededBloodType,
                        requestDate, validUntil, medicalCondition);
            } else {
                // Якщо запис у таблиці recipients не знайдено, створюємо новий запис
                Date currentDate = new Date(System.currentTimeMillis());
                // Дата валідності за замовчуванням: 30 днів вперед
                Date defaultValidUntil = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);

                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO recipients (id, name, surname, year, needed_blood_type, request_date, valid_until, medical_condition) " +
                                "VALUES (?, '', '', 0, '', ?, ?, '')");
                insertStmt.setInt(1, id);
                insertStmt.setDate(2, currentDate);
                insertStmt.setDate(3, defaultValidUntil);
                insertStmt.executeUpdate();

                // Повертаємо новий об'єкт
                return new Recipient(id, username, "", "", 0, "", currentDate, defaultValidUntil, "");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при отриманні даних реципієнта з бази: " + e.getMessage());
            return null;
        }
    }
}
