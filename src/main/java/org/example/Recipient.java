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

    public Recipient(int id, String username, String name, String surname, int year, String neededBloodType, Date requestDate, Date validUntil) {
        super(id, username, "RECIPIENT");
        this.name = name;
        this.surname = surname;
        this.year = year;
        this.neededBloodType = neededBloodType;
        this.requestDate = requestDate;
        this.validUntil = validUntil;
    }

    @Override
    public void showMenu() {
        System.out.println("🏥 Ви зайшли як Реципієнт.");
    }

    public static Recipient fromDatabase(int id, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM recipients WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Recipient(id, username, rs.getString("name"), rs.getString("surname"), rs.getInt("year"), rs.getString("needed_blood_type"), rs.getDate("request_date"), rs.getDate("valid_until"));
            }
        } catch (Exception e) {
            System.out.println("❌ Не вдалося завантажити реципієнта: " + e.getMessage());
        }
        return null;
    }
}

