package smartlib.ui;

import smartlib.model.User;
import smartlib.service.LoginManager;
import java.util.Scanner;

public class LoginMenu {
    private Scanner scanner;
    private LoginManager loginManager;

    public LoginMenu(Scanner scanner) {
        this.scanner = scanner;
        this.loginManager = new LoginManager();
    }

    public User showLoginScreen() {
        int attempts = 3;

        while (attempts > 0) {
            System.out.println("==================================================");
            System.out.println("            SMARTLIB – LOGIN SCREEN");
            System.out.println("==================================================");
            System.out.print("Enter Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            User loggedInUser = loginManager.login(username, password);

            if (loggedInUser != null) {
                System.out.println("\n[Success] Login successful! Welcome, " + loggedInUser.getUsername() 
                        + " [Role: " + loggedInUser.getRole() + "]");
                return loggedInUser;
            } else {
                attempts--;
                System.out.println("\n[Error] Invalid username or password.");
                if (attempts > 0) {
                    System.out.println("Attempts remaining: " + attempts + "\n");
                }
            }
        }

        System.out.println("\n[Error] 3 failed login attempts. Exiting application.");
        return null;
    }
}