package org.example;

import java.util.Scanner;

class User {
    protected String username;
    protected String role;
    protected Registration registration;

    public User(String username, String role, Registration registration) {
        this.username = username;
        this.role = role;
        this.registration = registration;
    }


    public void showMenu() {
        System.out.println("📌 Ви зайшли як " + role.toUpperCase());
        System.out.println("1 - Вийти");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        if (choice == 1) {
            System.out.println("📌 Ви вийшли з облікового запису.");
        } else {
            System.out.println("❌ Невірний вибір!");
        }
    }
}