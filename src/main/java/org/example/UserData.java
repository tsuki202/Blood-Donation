package org.example;

import lombok.*;

@Data
@AllArgsConstructor
public class UserData {
    private int id;
    private String username;
    private String password;
    private String role;

}