package smartlib.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
                "book_id TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "author TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "is_available INTEGER NOT NULL" +
                ");";

        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "department TEXT NOT NULL, " +
                "year TEXT NOT NULL, " +
                "email TEXT NOT NULL" +
                ");";

        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL" +
                ");";

        String createIssueRecordsTable = "CREATE TABLE IF NOT EXISTS issue_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id TEXT NOT NULL, " +
                "student_id TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "issue_date TEXT NOT NULL, " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id), " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id)" +
                ");";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("[Database Error] Connection is null, cannot initialize database.");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            // Enable foreign key support in SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute(createBooksTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createUsersTable);
            stmt.execute(createIssueRecordsTable);

            System.out.println("[Database] Tables initialized successfully.");

        } catch (SQLException e) {
            System.out.println("[Database Error] Failed to initialize database tables: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("[Database Error] Failed to close connection: " + e.getMessage());
            }
        }
    }
}