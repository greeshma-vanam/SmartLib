package smartlib.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;

    // Constructor to initialize the Scanner object
    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    // Method to display the loop and handle choices
    public void displayMenu() {
        int choice = 0;

        do {
            printHeader();
            printOptions();
            choice = getChoice();
            handleChoice(choice);

            if (choice != 9) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for user acknowledgment
            }
        } while (choice != 9);

        scanner.close();
    }

    // Displays the project header
    private void printHeader() {
        System.out.println("==================================================");
        System.out.println("      SMARTLIB – SMART LIBRARY MANAGEMENT SYSTEM");
        System.out.println("==================================================\n");
    }

    // Displays the main menu options
    private void printOptions() {
        System.out.println("1. Book Management");
        System.out.println("2. Student Management");
        System.out.println("3. Issue Book");
        System.out.println("4. Return Book");
        System.out.println("5. Search");
        System.out.println("6. Reports");
        System.out.println("7. Dashboard");
        System.out.println("8. AI Chatbot");
        System.out.println("9. Exit");
        System.out.print("\nEnter your choice (1-9): ");
    }

    // Handles user input and guards against non-integer inputs
    private int getChoice() {
        int choice = -1;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\n[Error] Invalid input! Please enter a number between 1 and 9.");
            choice = -1; // Keeps choice invalid to re-prompt
        } finally {
            scanner.nextLine(); // Clear the input buffer
        }
        return choice;
    }

    // Routes the user selection to placeholders or submenus
    private void handleChoice(int choice) {
        System.out.println("\n--------------------------------------------------");
        switch (choice) {
            case 1:
                BookMenu bookMenu = new BookMenu(scanner);
                bookMenu.displayBookMenu();
                break;
            case 2:
                System.out.println("[Selected] Student Management (Placeholder)");
                break;
            case 3:
                System.out.println("[Selected] Issue Book (Placeholder)");
                break;
            case 4:
                System.out.println("[Selected] Return Book (Placeholder)");
                break;
            case 5:
                System.out.println("[Selected] Search (Placeholder)");
                break;
            case 6:
                System.out.println("[Selected] Reports (Placeholder)");
                break;
            case 7:
                System.out.println("[Selected] Dashboard (Placeholder)");
                break;
            case 8:
                System.out.println("[Selected] AI Chatbot (Placeholder)");
                break;
            case 9:
                System.out.println("Exiting SmartLib. Thank you for using SmartLib!");
                break;
            default:
                System.out.println("[Error] Invalid choice. Please select an option between 1 and 9.");
        }
        System.out.println("--------------------------------------------------");
    }
}