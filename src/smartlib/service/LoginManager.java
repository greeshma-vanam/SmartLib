package smartlib.service;

import smartlib.model.User;
import java.util.ArrayList;

public class LoginManager {
    private ArrayList<User> users;

    public LoginManager() {
        users = new ArrayList<>();
        // Default accounts as required
        users.add(new User("admin", "admin123", "ADMIN"));
        users.add(new User("librarian", "lib123", "LIBRARIAN"));
        users.add(new User("student", "student123", "STUDENT"));
    }

    // Check credentials and return User if valid, else null
    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
}