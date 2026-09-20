package smartlib;

import smartlib.database.DatabaseInitializer;
import smartlib.model.User;
import smartlib.ui.LoginMenu;
import smartlib.ui.Menu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        LoginMenu loginMenu = new LoginMenu(scanner);

        User loggedInUser = loginMenu.showLoginScreen();

        if (loggedInUser != null) {
            Menu menu = new Menu(scanner, loggedInUser);
            menu.displayMenu();
        } else {
            scanner.close();
        }
    }
}