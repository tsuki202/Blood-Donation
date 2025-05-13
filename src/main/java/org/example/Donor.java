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

    public Donor(int id, String login, String name, String surname, int year, String bloodType, int weight, int height) {
        super(id, login, "DONOR");
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
            System.out.println("\n🩸 Ви зайшли як Донор " + login);
            if (name != null && !name.isEmpty()) {
                System.out.println("Ім'я: " + name + " " + surname);
            } else {
                System.out.println("⚠️ Ваш профіль не заповнений. Рекомендуємо заповнити дані.");
            }
            System.out.println("1 - Переглянути мої дані");
            System.out.println("2 - Заповнити/оновити персональні дані");
            System.out.println("3 - Заповнити анкету донора");
            System.out.println("4 - Переглянути історію донацій");
            System.out.println("5 - Записатися на донацію");
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
                case 3 -> fillDonorQuestionnaire();
                case 4 -> viewDonationHistory();
                case 5 -> scheduleDonation();
                case 6 -> System.out.println("🩸 Вихід з меню донора.");
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

        System.out.print("Введіть групу крові (наприклад, A+, B-, O+): ");
        String newBloodType = scanner.nextLine().trim();
        if (!newBloodType.isEmpty()) {
            this.bloodType = newBloodType;
        }

        System.out.print("Введіть вагу (кг): ");
        String weightStr = scanner.nextLine().trim();
        if (!weightStr.isEmpty() && weightStr.matches("\\d+")) {
            this.weight = Integer.parseInt(weightStr);
        }

        System.out.print("Введіть зріст (см): ");
        String heightStr = scanner.nextLine().trim();
        if (!heightStr.isEmpty() && heightStr.matches("\\d+")) {
            this.height = Integer.parseInt(heightStr);
        }

        // Зберігаємо дані в базу
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE donors SET name = ?, surname = ?, year = ?, blood_type = ?, weight = ?, height = ? WHERE id = ?");
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setInt(3, year);
            stmt.setString(4, bloodType);
            stmt.setInt(5, weight);
            stmt.setInt(6, height);
            stmt.setInt(7, id);

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
        System.out.println("Група крові: " + bloodType);
        System.out.println("Вага: " + weight + " кг");
        System.out.println("Зріст: " + height + " см");
    }

    private void fillDonorQuestionnaire() {
        System.out.println("\n📝 Анкета донора крові");

        Scanner scanner = new Scanner(System.in);

        // 1. Загальна інформація
        System.out.println("\n🔹 1. Загальна інформація");
        System.out.print("📌 Вік: ");
        int age;
        if (year > 0) {
            age = java.time.Year.now().getValue() - year;
            System.out.println(age);
        } else {
            System.out.print("Введіть ваш вік: ");
            age = Integer.parseInt(scanner.nextLine());
        }

        System.out.print("📌 Вага (кг): ");
        if (weight > 0) {
            System.out.println(weight);
        } else {
            this.weight = Integer.parseInt(scanner.nextLine());
        }

        System.out.print("📌 Зріст (см): ");
        if (height > 0) {
            System.out.println(height);
        } else {
            this.height = Integer.parseInt(scanner.nextLine());
        }

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

        // Оновлення даних донора в таблиці donors
        try (Connection conn = DatabaseManager.getConnection()) {
            // Спочатку оновлюємо інформацію в таблиці donors
            PreparedStatement updateDonorStmt = conn.prepareStatement(
                    "UPDATE donors SET weight = ?, height = ? WHERE id = ?");
            updateDonorStmt.setInt(1, weight);
            updateDonorStmt.setInt(2, height);
            updateDonorStmt.setInt(3, id);
            updateDonorStmt.executeUpdate();

            // Потім зберігаємо анкету
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
            System.out.println("Перевірте правильність формату дати (yyyy-mm-dd) та повторіть спробу.");
        }
    }

    public static Donor fromDatabase(int id, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Сначала проверяем существование записи
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM donors WHERE id = ?");
            checkStmt.setInt(1, id);
            ResultSet checkRs = checkStmt.executeQuery();

            boolean exists = checkRs.next() && checkRs.getInt(1) > 0;

            if (!exists) {
                // Если записи нет, создаем новую с базовыми значениями
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO donors (id, name, surname, year, blood_type, weight, height) VALUES (?, 'Новий', 'Донор', 2000, 'Невідомо', 70, 170)");
                insertStmt.setInt(1, id);
                insertStmt.executeUpdate();
                System.out.println("✅ Створено новий профіль донора.");
            }

            // Теперь получаем запись
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donors WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                int year = rs.getInt("year");
                String bloodType = rs.getString("blood_type");
                int weight = rs.getInt("weight");
                int height = rs.getInt("height");

                return new Donor(id, username, name, surname, year, bloodType, weight, height);
            }

            // Если мы все еще здесь, значит что-то пошло не так
            System.out.println("❌ Не вдалося завантажити профіль донора після створення запису.");
            return null;

        } catch (Exception e) {
            System.out.println("❌ Помилка при створенні профілю донора: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}