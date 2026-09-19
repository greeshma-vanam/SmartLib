package smartlib.ui;

import smartlib.service.BookManager;
import smartlib.service.StudentManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SearchMenu {
    private Scanner scanner;
    private BookManager bookManager;
    private StudentManager studentManager;

    public SearchMenu(Scanner scanner, BookManager bookManager, StudentManager studentManager) {
        this.scanner = scanner;
        this.bookManager = bookManager;
        this.studentManager = studentManager;
    }

    public void displaySearchMenu() {
        int choice = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("                   SEARCH MODULE");
            System.out.println("==================================================");
            System.out.println("1. Search Book");
            System.out.println("2. Search Student");
            System.out.println("3. Back");
            System.out.print("\nEnter your choice (1-3): ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
            } catch (InputMismatchException e) {
                System.out.println("\n[Error] Invalid input! Please enter a number between 1 and 3.");
                scanner.nextLine();
                choice = 0;
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n--- Search Book ---");
                    System.out.print("Enter Book ID or Title to search: ");
                    String bookQuery = scanner.nextLine().trim();
                    if (!bookQuery.isEmpty()) {
                        bookManager.searchBook(bookQuery);
                    } else {
                        System.out.println("[Error] Search query cannot be empty.");
                    }
                    break;
                case 2:
                    System.out.println("\n--- Search Student ---");
                    System.out.print("Enter Student ID or Name to search: ");
                    String studentQuery = scanner.nextLine().trim();
                    if (!studentQuery.isEmpty()) {
                        studentManager.searchStudent(studentQuery);
                    } else {
                        System.out.println("[Error] Search query cannot be empty.");
                    }
                    break;
                case 3:
                    System.out.println("\nReturning to Main Menu...");
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice. Please select between 1 and 3.");
            }

            if (choice != 3) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 3);
    }
}