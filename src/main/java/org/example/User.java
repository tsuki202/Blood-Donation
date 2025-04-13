package org.example;

import java.util.Scanner;
import lombok.*;
@AllArgsConstructor
public abstract class User implements UserAction {
    protected int id;
    protected String username;
    protected String role;


    protected void exit() {
        System.out.println("📌 Ви вийшли з акаунта.");
    }
}