package smartlib.service;

import smartlib.database.DatabaseConnection;
import smartlib.model.Book;

import java.sql.*;
import java.util.ArrayList;

public class BookManager {

    public BookManager() {
        ensureDefaultBooks();
    }

    private void ensureDefaultBooks() {
        if (getTotalBooksCount() == 0) {
            addBook("B001", "Java Programming", "John Smith", "Education");
            addBook("B002", "Data Structures", "Alice Johnson", "Computer Science");
        }
    }

    public boolean addBook(String bookId, String title, String author, String category) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "INSERT INTO books(book_id, title, author, category, is_available) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, category);
            pstmt.setInt(5, 1); // 1 for available

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
    }

    public void viewAllBooks() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("[Database Error] Cannot connect to database.");
            return;
        }

        String sql = "SELECT * FROM books";
        ArrayList<Book> books = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String bookId = rs.getString("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category");
                boolean isAvailable = rs.getInt("is_available") == 1;

                books.add(new Book(bookId, title, author, category, isAvailable));
            }

        } catch (SQLException e) {
            System.out.println("[Database Error] Failed to retrieve books: " + e.getMessage());
            return;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }

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

    public void searchBook(String query) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("[Database Error] Cannot connect to database.");
            return;
        }

        String sql = "SELECT * FROM books WHERE book_id = ? OR LOWER(title) LIKE ?";
        ArrayList<Book> books = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, query);
            pstmt.setString(2, "%" + query.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String bookId = rs.getString("book_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String category = rs.getString("category");
                    boolean isAvailable = rs.getInt("is_available") == 1;

                    books.add(new Book(bookId, title, author, category, isAvailable));
                }
            }

        } catch (SQLException e) {
            System.out.println("[Database Error] Failed to search books: " + e.getMessage());
            return;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }

        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("                                  SEARCH RESULTS");
        System.out.println("----------------------------------------------------------------------------------");
        if (books.isEmpty()) {
            System.out.println("[Info] No books match your search query: " + query);
        } else {
            for (Book b : books) {
                System.out.println(b);
            }
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    public boolean updateBook(String bookId, String newTitle, String newAuthor, String newCategory) {
        Book existing = findBookById(bookId);
        if (existing == null) {
            return false;
        }

        String titleToSave = newTitle.isEmpty() ? existing.getTitle() : newTitle;
        String authorToSave = newAuthor.isEmpty() ? existing.getAuthor() : newAuthor;
        String categoryToSave = newCategory.isEmpty() ? existing.getCategory() : newCategory;

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "UPDATE books SET title = ?, author = ?, category = ? WHERE book_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titleToSave);
            pstmt.setString(2, authorToSave);
            pstmt.setString(3, categoryToSave);
            pstmt.setString(4, bookId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
    }

    public boolean deleteBook(String bookId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
    }

    public Book findBookById(String bookId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("book_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String category = rs.getString("category");
                    boolean isAvailable = rs.getInt("is_available") == 1;

                    return new Book(id, title, author, category, isAvailable);
                }
            }
        } catch (SQLException e) {
            // Return null on error
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
        return null;
    }

    public int getTotalBooksCount() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Return 0 on error
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
        return 0;
    }

    public int getAvailableBooksCount() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM books WHERE is_available = 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Return 0 on error
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
        return 0;
    }

    public int getIssuedBooksCount() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM books WHERE is_available = 0";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Return 0 on error
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
        return 0;
    }
}