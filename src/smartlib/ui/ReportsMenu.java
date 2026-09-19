package smartlib.ui;

import smartlib.model.IssueRecord;
import smartlib.service.BookManager;
import smartlib.service.IssueManager;
import smartlib.service.StudentManager;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ReportsMenu {
    private Scanner scanner;
    private BookManager bookManager;
    private StudentManager studentManager;
    private IssueManager issueManager;

    public ReportsMenu(Scanner scanner, BookManager bookManager, StudentManager studentManager, IssueManager issueManager) {
        this.scanner = scanner;
        this.bookManager = bookManager;
        this.studentManager = studentManager;
        this.issueManager = issueManager;
    }

    public void displayReportsMenu() {
        int choice = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("                   REPORTS MODULE");
            System.out.println("==================================================");
            System.out.println("1. All Books Report");
            System.out.println("2. All Students Report");
            System.out.println("3. Issued Books Report");
            System.out.println("4. Back");
            System.out.print("\nEnter your choice (1-4): ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
            } catch (InputMismatchException e) {
                System.out.println("\n[Error] Invalid input! Please enter a number between 1 and 4.");
                scanner.nextLine();
                choice = 0;
                continue;
            }

            switch (choice) {
                case 1:
                    bookManager.viewAllBooks();
                    break;
                case 2:
                    studentManager.viewAllStudents();
                    break;
                case 3:
                    displayIssuedBooksReport();
                    break;
                case 4:
                    System.out.println("\nReturning to Main Menu...");
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice. Please select between 1 and 4.");
            }

            if (choice != 4) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 4);
    }

    private void displayIssuedBooksReport() {
        ArrayList<IssueRecord> records = issueManager.getIssueRecords();
        if (records.isEmpty()) {
            System.out.println("\n[Info] No books are currently issued.");
            return;
        }
        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("                                ISSUED BOOKS REPORT");
        System.out.println("----------------------------------------------------------------------------------");
        for (IssueRecord record : records) {
            System.out.println(record);
        }
        System.out.println("----------------------------------------------------------------------------------");
    }
}