package org.example;

public class LogIn {
    private Registration registration;

    public LogIn(Registration registration) {
        this.registration = registration;
    }

    public boolean loginUser(String username, String password) {
        if (!registration.userExists(username)) {
            System.out.println("Користувач не знайдений. Спочатку зареєструйтесь.");
            return false;
        }
        if (registration.getPassword(username).equals(password)) {
            String role = registration.getUserRole(username);
            System.out.println("✅ Вхід успішний! Ласкаво просимо, " + username);

            User user;
            if (role.equals("ADMIN")) {
                user = new Admin(username, registration);
            } else {
                user = new User(username, role, registration);
            }
            user.showMenu();
            return true;
        } else {
            System.out.println("❌ Неправильний пароль!");
            return false;
        }
    }
}