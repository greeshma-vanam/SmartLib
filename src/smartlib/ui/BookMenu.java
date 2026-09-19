package smartlib.ui;

import smartlib.model.Book;
import smartlib.service.BookManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BookMenu {
    private Scanner scanner;
    private BookManager bookManager;

    public BookMenu(Scanner scanner, BookManager bookManager) {
        this.scanner = scanner;
        this.bookManager = bookManager; // Use shared instance
    }

    public void displayBookMenu() {
        int choice = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("                 BOOK MANAGEMENT");
            System.out.println("==================================================");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Check Book Availability");
            System.out.println("7. Back to Main Menu");
            System.out.print("\nEnter your choice (1-7): ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
            } catch (InputMismatchException e) {
                System.out.println("\n[Error] Invalid input! Please enter a number between 1 and 7.");
                scanner.nextLine();
                choice = 0;
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddBook();
                    break;
                case 2:
                    bookManager.viewAllBooks();
                    break;
                case 3:
                    handleSearchBook();
                    break;
                case 4:
                    handleUpdateBook();
                    break;
                case 5:
                    handleDeleteBook();
                    break;
                case 6:
                    handleCheckAvailability();
                    break;
                case 7:
                    System.out.println("\nReturning to Main Menu...");
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice. Please select between 1 and 7.");
            }

            if (choice != 7) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 7);
    }

    private void handleAddBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("[Error] Book ID cannot be empty.");
            return;
        }

        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Author Name: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();

        boolean success = bookManager.addBook(id, title, author, category);
        if (success) {
            System.out.println("\n[Success] Book added successfully!");
        } else {
            System.out.println("\n[Error] Book ID already exists!");
        }
    }

    private void handleSearchBook() {
        System.out.println("\n--- Search Book ---");
        System.out.print("Enter Book ID or Title: ");
        String query = scanner.nextLine().trim();
        if (!query.isEmpty()) {
            bookManager.searchBook(query);
        }
    }

    private void handleUpdateBook() {
        System.out.println("\n--- Update Book ---");
        System.out.print("Enter Book ID to update: ");
        String id = scanner.nextLine().trim();
        Book b = bookManager.findBookById(id);
        if (b == null) {
            System.out.println("[Error] Book ID not found.");
            return;
        }
        System.out.print("Enter new Title (or press Enter to skip): ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter new Author (or press Enter to skip): ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter new Category (or press Enter to skip): ");
        String category = scanner.nextLine().trim();

        if (bookManager.updateBook(id, title, author, category)) {
            System.out.println("\n[Success] Book updated successfully!");
        }
    }

    private void handleDeleteBook() {
        System.out.println("\n--- Delete Book ---");
        System.out.print("Enter Book ID to delete: ");
        String id = scanner.nextLine().trim();
        if (bookManager.deleteBook(id)) {
            System.out.println("\n[Success] Book deleted successfully!");
        } else {
            System.out.println("[Error] Book ID not found.");
        }
    }

    private void handleCheckAvailability() {
        System.out.println("\n--- Check Book Availability ---");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine().trim();
        Book b = bookManager.findBookById(id);
        if (b != null) {
            System.out.println("Status: " + (b.isAvailable() ? "Available" : "Issued"));
        } else {
            System.out.println("[Error] Book ID not found.");
        }
    }
}