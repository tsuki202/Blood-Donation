package org.example;

import lombok.*;

@Getter
@Setter
@Data
public abstract class User implements UserAction {
    protected int id;
    protected String login;
    protected String role;

    public User(int id, String login, String role) {
        this.id = id;
        this.login = login;
        this.role = role;
    }

    protected void exit() {
        System.out.println("📌 Ви вийшли з акаунта.");
    }
}