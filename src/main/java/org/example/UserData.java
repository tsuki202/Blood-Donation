package org.example;

import lombok.*;

@Data
@AllArgsConstructor
class UserData {
    private int id;
    private String username;
    private String password;
    private String role;
}