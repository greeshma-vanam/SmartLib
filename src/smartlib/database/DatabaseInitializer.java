package smartlib.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initializeDatabase() {

        String createBooksTable =
                "CREATE TABLE IF NOT EXISTS books (" +
                "book_id TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "author TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "is_available INTEGER NOT NULL" +
                ");";

        String createStudentsTable =
                "CREATE TABLE IF NOT EXISTS students (" +
                "student_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "department TEXT NOT NULL, " +
                "year TEXT NOT NULL, " +
                "email TEXT NOT NULL" +
                ");";

        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL" +
                ");";

        String createIssueRecordsTable =
                "CREATE TABLE IF NOT EXISTS issue_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id TEXT NOT NULL, " +
                "student_id TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "issue_date TEXT NOT NULL, " +
                "return_date TEXT, " +
                "fine_amount INTEGER DEFAULT 0, " +
                "payment_status TEXT DEFAULT 'NO_FINE', " +
                "payment_date TEXT, " +
                "payment_method TEXT, " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id), " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id)" +
                ");";

        String createNotificationsTable =
                "CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipient_id TEXT NOT NULL, " +
                "recipient_role TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "notification_type TEXT NOT NULL, " +
                "is_read INTEGER DEFAULT 0, " +
                "created_at TEXT NOT NULL" +
                ");";

        String createNotificationSettingsTable =
                "CREATE TABLE IF NOT EXISTS notification_settings (" +
                "username TEXT PRIMARY KEY, " +
                "desktop_notifications INTEGER DEFAULT 1, " +
                "permission_asked INTEGER DEFAULT 0" +
                ");";

        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            System.out.println(
                    "[Database Error] Connection is null."
            );
            return;
        }

        try (Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // =====================================================
            // CREATE TABLES
            // =====================================================

            stmt.execute(createBooksTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createUsersTable);
            stmt.execute(createIssueRecordsTable);

            addColumnIfMissing(
                    stmt,
                    "issue_records",
                    "return_date",
                    "TEXT"
            );

            addColumnIfMissing(
                    stmt,
                    "issue_records",
                    "fine_amount",
                    "INTEGER DEFAULT 0"
            );

            addColumnIfMissing(
                    stmt,
                    "issue_records",
                    "payment_status",
                    "TEXT DEFAULT 'NO_FINE'"
            );

            addColumnIfMissing(
                    stmt,
                    "issue_records",
                    "payment_date",
                    "TEXT"
            );

            addColumnIfMissing(
                    stmt,
                    "issue_records",
                    "payment_method",
                    "TEXT"
            );

            stmt.execute(createNotificationsTable);
            stmt.execute(createNotificationSettingsTable);

            // =====================================================
            // DEFAULT BOOKS
            // =====================================================

            stmt.execute(
                    "INSERT OR IGNORE INTO books " +
                    "(book_id, title, author, category, is_available) " +
                    "VALUES " +
                    "('B001', 'Java Programming', 'John Smith', " +
                    "'Education', 1)"
            );

            stmt.execute(
                    "INSERT OR IGNORE INTO books " +
                    "(book_id, title, author, category, is_available) " +
                    "VALUES " +
                    "('B002', 'Data Structures', 'Alice Johnson', " +
                    "'Computer Science', 1)"
            );

            // =====================================================
            // DEFAULT STUDENTS
            // =====================================================

            stmt.execute(
                    "INSERT OR IGNORE INTO students " +
                    "(student_id, name, department, year, email) " +
                    "VALUES " +
                    "('S001', 'Rahul Sharma', 'CSE-AIML', " +
                    "'3rd', 'rahul@smartlib.com')"
            );

            stmt.execute(
                    "INSERT OR IGNORE INTO students " +
                    "(student_id, name, department, year, email) " +
                    "VALUES " +
                    "('S002', 'Priya Verma', 'CSE', " +
                    "'3rd', 'priya@smartlib.com')"
            );

            // =====================================================
            // DEFAULT USERS
            // =====================================================

            stmt.execute(
                    "INSERT OR IGNORE INTO users " +
                    "(username, password, role) " +
                    "VALUES ('admin', 'admin123', 'ADMIN')"
            );

            stmt.execute(
                    "INSERT OR IGNORE INTO users " +
                    "(username, password, role) " +
                    "VALUES ('librarian', 'lib123', 'LIBRARIAN')"
            );

            stmt.execute(
                    "INSERT OR IGNORE INTO users " +
                    "(username, password, role) " +
                    "VALUES ('student', 'student123', 'STUDENT')"
            );

            System.out.println(
                    "[Database] Tables and default data initialized successfully."
            );

        } catch (SQLException e) {

            System.out.println(
                    "[Database Error] Failed to initialize database: "
                    + e.getMessage()
            );

        } finally {

            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    // =========================================================
    // ADD COLUMN IF MISSING
    // =========================================================

    private static void addColumnIfMissing(
            Statement stmt,
            String table,
            String column,
            String definition
    ) {

        try {

            stmt.execute(
                    "ALTER TABLE " +
                    table +
                    " ADD COLUMN " +
                    column +
                    " " +
                    definition
            );

            System.out.println(
                    "[Database] Added column: " +
                    table + "." + column
            );

        } catch (SQLException e) {

            if (!e.getMessage()
                    .toLowerCase()
                    .contains("duplicate column")) {

                System.out.println(
                        "[Database] Could not add column " +
                        column + ": " +
                        e.getMessage()
                );
            }
        }
    }
}