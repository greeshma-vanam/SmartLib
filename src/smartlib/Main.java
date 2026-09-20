package smartlib;

import smartlib.model.User;
import smartlib.ui.LoginMenu;
import smartlib.ui.Menu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginMenu loginMenu = new LoginMenu(scanner);

        // Start with the Login Screen
        User loggedInUser = loginMenu.showLoginScreen();

        if (loggedInUser != null) {
            // Pass the scanner and authenticated user to the main menu
            Menu menu = new Menu(scanner, loggedInUser);
            menu.displayMenu();
        } else {
            scanner.close();
        }
    }
}