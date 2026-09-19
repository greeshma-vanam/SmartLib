package smartlib.service;

import smartlib.model.Book;
import java.util.ArrayList;

public class BookManager {
    private ArrayList<Book> books;

    public BookManager() {
        books = new ArrayList<>();
        // Pre-loaded sample books for easy testing
        books.add(new Book("B001", "Java Programming", "John Smith", "Education", true));
        books.add(new Book("B002", "Data Structures", "Alice Johnson", "Computer Science", true));
    }

    // Add a new book
    public boolean addBook(String bookId, String title, String author, String category) {
        // Check for duplicate Book ID
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) {
                return false; 
            }
        }
        books.add(new Book(bookId, title, author, category, true));
        return true;
    }

    // View all books
    public void viewAllBooks() {
        if (books.isEmpty()) {
            System.out.println("\n[Info] No books found in the library.");
            return;
        }
        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("                                  ALL BOOKS LIST");
        System.out.println("----------------------------------------------------------------------------------");
        for (Book b : books) {
            System.out.println(b);
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    // Search book by ID or Title
    public void searchBook(String query) {
        boolean found = false;
        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("                                  SEARCH RESULTS");
        System.out.println("----------------------------------------------------------------------------------");
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(query) || b.getTitle().toLowerCase().contains(query.toLowerCase())) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) {
            System.out.println("[Info] No books match your search query: " + query);
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    // Update book details
    public boolean updateBook(String bookId, String newTitle, String newAuthor, String newCategory) {
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) {
                if (!newTitle.isEmpty()) b.setTitle(newTitle);
                if (!newAuthor.isEmpty()) b.setAuthor(newAuthor);
                if (!newCategory.isEmpty()) b.setCategory(newCategory);
                return true;
            }
        }
        return false;
    }

    // Delete a book by ID
    public boolean deleteBook(String bookId) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId().equalsIgnoreCase(bookId)) {
                books.remove(i);
                return true;
            }
        }
        return false;
    }

    // Find and return a book object for availability checking or referencing
    public Book findBookById(String bookId) {
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) {
                return b;
            }
        }
        return null;
    }
}