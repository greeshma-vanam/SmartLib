package smartlib.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private boolean isAvailable; // true = Available, false = Issued

    // Constructor
    public Book(String bookId, String title, String author, String category, boolean isAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters (Encapsulation)
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // toString method to display book details neatly
    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Issued";
        return String.format("ID: %-6s | Title: %-22s | Author: %-15s | Category: %-15s | Status: %s",
                bookId, title, author, category, status);
    }
}