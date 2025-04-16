package org.example;

import lombok.*;

@Getter
@Setter
public abstract class User implements UserAction {
    protected int id;
    protected String username;
    protected String role;

    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    protected void exit() {
        System.out.println("📌 Ви вийшли з акаунта.");
    }
}