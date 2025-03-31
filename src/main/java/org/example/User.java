package org.example;

import java.util.Scanner;
import lombok.*;
@AllArgsConstructor
abstract class User implements UserAction{
    protected String username;
    protected String role;
    protected Registration registration;
    protected void exit() {
        System.out.println("📌 Ви вийшли з акаунта.");
    }

//    public void showMenu() {
//        System.out.println("📌 Ви зайшли як " + role.toUpperCase());
//        System.out.println("1 - Вийти");
//
//        Scanner scanner = new Scanner(System.in);
//        int choice = scanner.nextInt();
//        scanner.nextLine(); // Очистка буфера
//
//        if (choice == 1) {
//            System.out.println("📌 Ви вийшли з облікового запису.");
//        } else {
//            System.out.println("❌ Невірний вибір!");
//        }
//    }
}