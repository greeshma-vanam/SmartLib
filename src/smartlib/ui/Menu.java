package smartlib.ui;

import smartlib.service.BookManager;
import smartlib.service.IssueManager;
import smartlib.service.StudentManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private BookManager bookManager;
    private StudentManager studentManager;
    private IssueManager issueManager;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.bookManager = new BookManager();
        this.studentManager = new StudentManager();
        this.issueManager = new IssueManager(bookManager, studentManager);
    }

    public void displayMenu() {
        int choice = 0;

        do {
            printHeader();
            printOptions();
            choice = getChoice();
            handleChoice(choice);

            if (choice != 9) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        } while (choice != 9);

        scanner.close();
    }

    private void printHeader() {
        System.out.println("==================================================");
        System.out.println("      SMARTLIB – SMART LIBRARY MANAGEMENT SYSTEM");
        System.out.println("==================================================\n");
    }

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

    private int getChoice() {
        int choice = -1;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\n[Error] Invalid input! Please enter a number between 1 and 9.");
            choice = -1;
        } finally {
            scanner.nextLine();
        }
        return choice;
    }

    private void handleChoice(int choice) {
        System.out.println("\n--------------------------------------------------");
        switch (choice) {
            case 1:
                BookMenu bookMenu = new BookMenu(scanner, bookManager);
                bookMenu.displayBookMenu();
                break;
            case 2:
                StudentMenu studentMenu = new StudentMenu(scanner, studentManager);
                studentMenu.displayStudentMenu();
                break;
            case 3:
                handleIssueBook();
                break;
            case 4:
                handleReturnBook();
                break;
            case 5:
                SearchMenu searchMenu = new SearchMenu(scanner, bookManager, studentManager);
                searchMenu.displaySearchMenu();
                break;
            case 6:
                ReportsMenu reportsMenu = new ReportsMenu(scanner, bookManager, studentManager, issueManager);
                reportsMenu.displayReportsMenu();
                break;
            case 7:
                handleDashboard();
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

    private void handleIssueBook() {
        System.out.println("               ISSUE BOOK TO STUDENT");
        System.out.println("--------------------------------------------------");
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine().trim();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        issueManager.issueBook(bookId, studentId);
    }

    private void handleReturnBook() {
        System.out.println("                 RETURN BOOK");
        System.out.println("--------------------------------------------------");
        System.out.print("Enter Book ID to return: ");
        String bookId = scanner.nextLine().trim();

        issueManager.returnBook(bookId);
    }

    // Dashboard Handler (Option 7)
    private void handleDashboard() {
        System.out.println("                   LIBRARY DASHBOARD");
        System.out.println("--------------------------------------------------");
        System.out.println("Total Books     : " + bookManager.getTotalBooksCount());
        System.out.println("Available Books : " + bookManager.getAvailableBooksCount());
        System.out.println("Issued Books    : " + bookManager.getIssuedBooksCount());
        System.out.println("Total Students  : " + studentManager.getTotalStudentsCount());
        System.out.println("--------------------------------------------------");
    }
}