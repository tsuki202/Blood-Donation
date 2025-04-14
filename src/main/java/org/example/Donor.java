package org.example;

import java.sql.*;
import java.util.Scanner;

@Role("DONOR")
public class Donor extends User {
    private String name;
    private String surname;
    private int year;
    private String bloodType;
    private int weight;
    private int height;

    public Donor(int id, String username, String name, String surname, int year, String bloodType, int weight, int height) {
        super(id, username, "DONOR");
        this.name = name;
        this.surname = surname;
        this.year = year;
        this.bloodType = bloodType;
        this.weight = weight;
        this.height = height;
    }

    @Override
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n🩸 Ви зайшли як Донор " + name + " " + surname);
            System.out.println("1 - Переглянути мої дані");
            System.out.println("2 - Заповнити анкету донора");
            System.out.println("3 - Переглянути історію донацій");
            System.out.println("4 - Записатися на донацію");
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
                case 2 -> fillDonorQuestionnaire();
                case 3 -> viewDonationHistory();
                case 4 -> scheduleDonation();
                case 5 -> System.out.println("🩸 Вихід з меню донора.");
                default -> System.out.println("❌ Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 5);
    }

    private void viewMyData() {
        System.out.println("\n📋 Ваші дані:");
        System.out.println("Ім'я: " + name);
        System.out.println("Прізвище: " + surname);
        System.out.println("Рік народження: " + year);
        System.out.println("Група крові: " + bloodType);
        System.out.println("Вага: " + weight + " кг");
        System.out.println("Зріст: " + height + " см");
    }

    private void fillDonorQuestionnaire() {
        System.out.println("\n📝 Анкета донора крові");

        Scanner scanner = new Scanner(System.in);

        // 1. Загальна інформація
        System.out.println("\n🔹 1. Загальна інформація");
        System.out.print("📌 Вік: " + (2023 - year));
        System.out.print("\n📌 Вага (кг): ");
        this.weight = Integer.parseInt(scanner.nextLine());
        System.out.print("📌 Зріст (см): ");
        this.height = Integer.parseInt(scanner.nextLine());

        // 2. Загальний стан здоров'я
        System.out.println("\n🔹 2. Загальний стан здоров'я");
        System.out.print("✅ Чи відчуваєте ви себе добре сьогодні? (Так/Ні): ");
        String feelsGood = scanner.nextLine();
        System.out.print("✅ Чи були у вас останнім часом симптоми застуди, грипу? (Так/Ні): ");
        String hadSymptoms = scanner.nextLine();
        System.out.print("✅ Чи приймаєте ви зараз ліки? (Якщо так, вкажіть які): ");
        String medications = scanner.nextLine();

        // 3. Хронічні захворювання
        System.out.println("\n🔹 3. Хронічні та серйозні захворювання");
        System.out.print("✅ Чи маєте ви серцево-судинні захворювання, діабет? (Так/Ні): ");
        String chronicDiseases = scanner.nextLine();
        System.out.print("✅ Чи маєте ви алергію на медикаменти? (Так/Ні): ");
        String allergies = scanner.nextLine();

        // 4. Поведінкові фактори
        System.out.println("\n🔹 4. Поведінкові фактори");
        System.out.print("✅ Чи робили вам операції за останні 6 місяців? (Так/Ні): ");
        String recentSurgeries = scanner.nextLine();
        System.out.print("✅ Чи були у вас татуювання за останні 6 місяців? (Так/Ні): ");
        String tattoos = scanner.nextLine();
        System.out.print("✅ Чи вживали ви алкоголь останні 48 годин? (Так/Ні): ");
        String alcohol = scanner.nextLine();

        // Збереження анкети в базу даних
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO donor_questionnaires (donor_id, weight, height, feels_good, had_symptoms, medications, " +
                            "chronic_diseases, allergies, recent_surgeries, tattoos, alcohol) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, id);
            stmt.setInt(2, weight);
            stmt.setInt(3, height);
            stmt.setString(4, feelsGood);
            stmt.setString(5, hadSymptoms);
            stmt.setString(6, medications);
            stmt.setString(7, chronicDiseases);
            stmt.setString(8, allergies);
            stmt.setString(9, recentSurgeries);
            stmt.setString(10, tattoos);
            stmt.setString(11, alcohol);

            stmt.executeUpdate();
            System.out.println("\n✅ Анкету успішно збережено!");
        } catch (Exception e) {
            System.out.println("❌ Помилка при збереженні анкети: " + e.getMessage());
        }
    }

    private void viewDonationHistory() {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT donation_date, amount, location FROM donations WHERE donor_id = ? ORDER BY donation_date DESC");
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            System.out.println("\n📅 Історія ваших донацій:");
            boolean hasDonations = false;

            while (rs.next()) {
                hasDonations = true;
                System.out.println("\nДата: " + rs.getDate("donation_date"));
                System.out.println("Об'єм: " + rs.getInt("amount") + " мл");
                System.out.println("Місце: " + rs.getString("location"));
            }

            if (!hasDonations) {
                System.out.println("У вас ще немає історії донацій.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка при отриманні історії донацій: " + e.getMessage());
        }
    }

    private void scheduleDonation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n📅 Запис на донацію крові");

        System.out.print("Введіть бажану дату (yyyy-mm-dd): ");
        String date = scanner.nextLine();

        System.out.print("Введіть бажане місце: ");
        String location = scanner.nextLine();

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO donation_schedule (donor_id, scheduled_date, location, status) VALUES (?, ?, ?, 'pending')");

            stmt.setInt(1, id);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, location);

            stmt.executeUpdate();
            System.out.println("\n✅ Ви успішно записались на донацію!");
        } catch (Exception e) {
            System.out.println("❌ Помилка при записі на донацію: " + e.getMessage());
        }
    }

    public static Donor fromDatabase(int id, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donors WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Donor(
                        id, username,
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("year"),
                        rs.getString("blood_type"),
                        rs.getInt("weight"),
                        rs.getInt("height")
                );
            }
        } catch (Exception e) {
            System.out.println("❌ Не вдалося завантажити донора: " + e.getMessage());
        }
        return null;
    }
}