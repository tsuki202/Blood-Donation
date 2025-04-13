package org.example;

import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.Scanner;
@Role("DONOR")
public class Donor extends User {
    private String name;
    private String surname;
    private int year;
    private String bloodType;

    public Donor(int id, String username, String name, String surname, int year, String bloodType) {
        super(id, username, "DONOR");
        this.name = name;
        this.surname = surname;
        this.year = year;
        this.bloodType = bloodType;
    }

    @Override
    public void showMenu() {
        System.out.println("🩸 Ви зайшли як Донор.");
    }

    public static Donor fromDatabase(int id, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donors WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Donor(id, username, rs.getString("name"), rs.getString("surname"), rs.getInt("year"), rs.getString("blood_type"));
            }
        } catch (Exception e) {
            System.out.println("❌ Не вдалося завантажити донора: " + e.getMessage());
        }
        return null;
    }
}

