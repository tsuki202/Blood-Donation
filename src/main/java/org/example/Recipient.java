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
            System.out.println("\n🏥 Ви зайшли як Реципієнт " + name + " " + surname);
            System.out.println("1 - Переглянути мої дані");
            System.out.println("2 - Пошук донорів за групою крові");
            System.out.println("3 - Перевірити статус запиту");
            System.out.println("4 - Оновити медичну інформацію");
            System.out.println("5 - Вийти");
            System.out.print("Оберіть дію: ");

            while (!scanner.hasNextInt()) {
                System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
                scanner.next();
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMyData();
                case 2 -> searchDonorsByBloodType();
                case 3 -> checkRequestStatus();
                case 4 -> updateMedicalInfo();
                case 5 -> System.out.println("🏥 Вихід з меню реципієнта.");
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 5);
    }

    private void viewMyData() {
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
        System.out.println("\n🔍 Пошук донорів з групою крові: " + neededBloodType);

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT d.id, d.name, d.surname, d.year, d.blood_type, d.weight, d.height " +
                            "FROM donors d " +
                            "WHERE d.blood_type = ? AND d.id IN (" +
                            "  SELECT q.donor_id FROM donor_questionnaires q " +
                            "  WHERE q.feels_good = 'Так' AND q.had_symptoms = 'Ні' " +
                            "  AND q.recent_surgeries = 'Ні' AND q.tattoos = 'Ні' " +
                            "  AND q.alcohol = 'Ні'" +
                            ")");

            stmt.setString(1, neededBloodType);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n🩸 Доступні донори:");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("\nID: " + rs.getInt("id"));
                System.out.println("Ім'я: " + rs.getString("name") + " " + rs.getString("surname"));
                System.out.println("Вік: " + (2023 - rs.getInt("year")));
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
                System.out.println("\nℹ️ Ваш запит ще обробляється.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при перевірці статусу: " + e.getMessage());
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
            this.validUntil = Date.valueOf(newDate);
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recipients SET needed_blood_type = ?, medical_condition = ?, valid_until = ? WHERE id = ?");

            stmt.setString(1, neededBloodType);
            stmt.setString(2, medicalCondition);
            stmt.setDate(3, validUntil);
            stmt.setInt(4, id);

            stmt.executeUpdate();
            System.out.println("\n✅ Медичну інформацію успішно оновлено!");
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
                return new Recipient(
                        id, username,
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("year"),
                        rs.getString("needed_blood_type"),
                        rs.getDate("request_date"),
                        rs.getDate("valid_until"),
                        rs.getString("medical_condition")
                );
            }
        } catch (Exception e) {
            System.out.println("❌ Не вдалося завантажити реципієнта: " + e.getMessage());
        }
        return null;
    }
}