package smartlib.service;

import smartlib.database.DatabaseConnection;
import smartlib.model.Book;
import smartlib.model.IssueRecord;
import smartlib.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class IssueManager {

    private BookManager bookManager;
    private StudentManager studentManager;

    public IssueManager(
            BookManager bookManager,
            StudentManager studentManager
    ) {
        this.bookManager = bookManager;
        this.studentManager = studentManager;
    }

    // =========================================================
    // ISSUE BOOK
    // =========================================================

    public boolean issueBook(
            String bookId,
            String studentId
    ) {

        Book book =
                bookManager.findBookById(bookId);

        if (book == null) {

            System.out.println(
                    "[Error] Book ID does not exist in the library."
            );

            return false;
        }

        Student student =
                studentManager.findStudentById(studentId);

        if (student == null) {

            System.out.println(
                    "[Error] Student ID does not exist in the system."
            );

            return false;
        }

        if (!book.isAvailable()) {

            System.out.println(
                    "[Error] Book is already issued to another student."
            );

            return false;
        }

        LocalDate issueDate =
                LocalDate.now();

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {

            System.out.println(
                    "[Database Error] Cannot connect to database."
            );

            return false;
        }

        String insertSql =
                "INSERT INTO issue_records " +
                "(book_id, student_id, status, issue_date, " +
                "fine_amount, payment_status) " +
                "VALUES (?, ?, ?, ?, 0, 'NO_FINE')";

        String updateBookSql =
                "UPDATE books SET is_available = 0 " +
                "WHERE book_id = ?";

        try {

            conn.setAutoCommit(false);

            try (
                    PreparedStatement pstmt1 =
                            conn.prepareStatement(insertSql);

                    PreparedStatement pstmt2 =
                            conn.prepareStatement(updateBookSql)
            ) {

                pstmt1.setString(1, bookId);
                pstmt1.setString(2, studentId);
                pstmt1.setString(3, "ISSUED");
                pstmt1.setString(
                        4,
                        issueDate.toString()
                );

                pstmt1.executeUpdate();

                pstmt2.setString(1, bookId);
                pstmt2.executeUpdate();

                conn.commit();

                System.out.println(
                        "[Info] Book issued successfully on: "
                                + issueDate
                );

                // Notify the student
                NotificationManager.createNotification(
                        studentId,
                        "STUDENT",
                        "Book Issued",
                        "The book \"" +
                                book.getTitle() +
                                "\" has been issued to you. " +
                                "Please return it within 7 days.",
                        "BOOK_ISSUED"
                );

                return true;

            } catch (SQLException e) {

                conn.rollback();

                System.out.println(
                        "[Database Error] Failed to issue book: "
                                + e.getMessage()
                );

                return false;
            }

        } catch (SQLException e) {

            System.out.println(
                    "[Database Error] Transaction error: "
                            + e.getMessage()
            );

            return false;

        } finally {

            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }


    // =========================================================
    // RETURN BOOK
    // =========================================================

    public boolean returnBook(String bookId) {

        Book book =
                bookManager.findBookById(bookId);

        if (book == null) {

            System.out.println(
                    "[Error] Book ID does not exist in the library."
            );

            return false;
        }

        if (book.isAvailable()) {

            System.out.println(
                    "[Error] This book is already available " +
                    "in the library (not currently issued)."
            );

            return false;
        }

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {

            System.out.println(
                    "[Database Error] Cannot connect to database."
            );

            return false;
        }

        String selectSql =
                "SELECT * FROM issue_records " +
                "WHERE book_id = ? " +
                "AND status = 'ISSUED' " +
                "LIMIT 1";

        String updateIssueSql =
                "UPDATE issue_records SET " +
                "status = 'RETURNED', " +
                "return_date = ?, " +
                "fine_amount = ?, " +
                "payment_status = ? " +
                "WHERE id = ?";

        String updateBookSql =
                "UPDATE books SET is_available = 1 " +
                "WHERE book_id = ?";

        LocalDate issueDate = null;
        int issueRecordId = -1;
        String studentId = null;

        try {

            // -------------------------------------------------
            // Find active issue record
            // -------------------------------------------------

            try (
                    PreparedStatement pstmtSelect =
                            conn.prepareStatement(selectSql)
            ) {

                pstmtSelect.setString(1, bookId);

                try (
                        ResultSet rs =
                                pstmtSelect.executeQuery()
                ) {

                    if (rs.next()) {

                        issueRecordId =
                                rs.getInt("id");

                        studentId =
                                rs.getString("student_id");

                        issueDate =
                                LocalDate.parse(
                                        rs.getString(
                                                "issue_date"
                                        )
                                );
                    }
                }
            }

            if (issueDate == null ||
                    issueRecordId == -1 ||
                    studentId == null) {

                System.out.println(
                        "[Error] No active issue record found " +
                        "for this Book ID."
                );

                conn.close();

                return false;
            }

            LocalDate returnDate =
                    LocalDate.now();

            long daysKept =
                    ChronoUnit.DAYS.between(
                            issueDate,
                            returnDate
                    );

            if (daysKept < 0) {
                daysKept = 0;
            }

            long lateDays =
                    daysKept > 7
                            ? daysKept - 7
                            : 0;

            long fineAmount =
                    lateDays * 5;

            String paymentStatus =
                    fineAmount > 0
                            ? "UNPAID"
                            : "NO_FINE";

            conn.setAutoCommit(false);

            try (
                    PreparedStatement pstmtUpdateIssue =
                            conn.prepareStatement(updateIssueSql);

                    PreparedStatement pstmtUpdateBook =
                            conn.prepareStatement(updateBookSql)
            ) {

                // Update issue record
                pstmtUpdateIssue.setString(
                        1,
                        returnDate.toString()
                );

                pstmtUpdateIssue.setLong(
                        2,
                        fineAmount
                );

                pstmtUpdateIssue.setString(
                        3,
                        paymentStatus
                );

                pstmtUpdateIssue.setInt(
                        4,
                        issueRecordId
                );

                pstmtUpdateIssue.executeUpdate();

                // Make book available
                pstmtUpdateBook.setString(
                        1,
                        bookId
                );

                pstmtUpdateBook.executeUpdate();

                conn.commit();

                // -------------------------------------------------
                // Console summary
                // -------------------------------------------------

                System.out.println(
                        "\n--------------------------------------------------"
                );

                System.out.println(
                        "                 RETURN & FINE SUMMARY"
                );

                System.out.println(
                        "--------------------------------------------------"
                );

                System.out.println(
                        "Book ID        : " + bookId
                );

                System.out.println(
                        "Student ID     : " + studentId
                );

                System.out.println(
                        "Issue Date     : " + issueDate
                );

                System.out.println(
                        "Return Date    : " + returnDate
                );

                System.out.println(
                        "Days Kept      : " + daysKept
                );

                System.out.println(
                        "Late Days      : " + lateDays
                );

                System.out.println(
                        "Fine Amount    : Rs." + fineAmount
                );

                System.out.println(
                        "Payment Status : " + paymentStatus
                );

                System.out.println(
                        "--------------------------------------------------"
                );

                // -------------------------------------------------
                // Student notification
                // -------------------------------------------------

                if (fineAmount > 0) {

                    NotificationManager.createNotification(
                            studentId,
                            "STUDENT",
                            "Fine Pending",
                            "You have a fine of ₹" +
                                    fineAmount +
                                    " for \"" +
                                    book.getTitle() +
                                    "\".",
                            "FINE_PENDING"
                    );

                } else {

                    NotificationManager.createNotification(
                            studentId,
                            "STUDENT",
                            "Book Returned",
                            "Your book \"" +
                                    book.getTitle() +
                                    "\" has been returned successfully.",
                            "BOOK_RETURNED"
                    );
                }

                // -------------------------------------------------
                // Admin / Librarian notifications
                // -------------------------------------------------

                notifyStaffBookReturned(
                        book.getTitle(),
                        studentId,
                        fineAmount
                );

                return true;

            } catch (SQLException e) {

                conn.rollback();

                System.out.println(
                        "[Database Error] Failed to return book: "
                                + e.getMessage()
                );

                return false;
            }

        } catch (SQLException e) {

            System.out.println(
                    "[Database Error] Database error during return: "
                            + e.getMessage()
            );

            return false;

        } finally {

            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }


    // =========================================================
    // NOTIFY ADMIN + LIBRARIAN
    // =========================================================

    private void notifyStaffBookReturned(
            String bookTitle,
            String studentId,
            long fineAmount
    ) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return;
        }

        String sql =
                "SELECT username, role FROM users " +
                "WHERE role = 'ADMIN' " +
                "OR role = 'LIBRARIAN'";

        try (
                Statement stmt =
                        conn.createStatement();

                ResultSet rs =
                        stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                String username =
                        rs.getString("username");

                String role =
                        rs.getString("role");

                String message;

                if (fineAmount > 0) {

                    message =
                            "\"" +
                            bookTitle +
                            "\" was returned by student " +
                            studentId +
                            ". Fine pending: ₹" +
                            fineAmount +
                            ".";

                } else {

                    message =
                            "\"" +
                            bookTitle +
                            "\" was returned by student " +
                            studentId +
                            ". The book is now available.";
                }

                NotificationManager.createNotification(
                        username,
                        role,
                        "Book Returned",
                        message,
                        "BOOK_RETURNED"
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "[Notification Error] " +
                    e.getMessage()
            );

        } finally {

            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }


    // =========================================================
    // GET CURRENTLY ISSUED BOOKS
    // =========================================================

    public ArrayList<IssueRecord> getIssueRecords() {

        ArrayList<IssueRecord> records =
                new ArrayList<>();

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return records;
        }

        String sql =
                "SELECT * FROM issue_records " +
                "WHERE status = 'ISSUED' " +
                "ORDER BY issue_date DESC";

        try (
                Statement stmt =
                        conn.createStatement();

                ResultSet rs =
                        stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                String bookId =
                        rs.getString("book_id");

                String studentId =
                        rs.getString("student_id");

                String status =
                        rs.getString("status");

                String issueDateStr =
                        rs.getString("issue_date");

                IssueRecord record =
                        new IssueRecord(
                                bookId,
                                studentId,
                                status
                        );

                if (
                        issueDateStr != null &&
                        !issueDateStr.isEmpty()
                ) {

                    record.setIssueDate(
                            LocalDate.parse(
                                    issueDateStr
                            )
                    );
                }

                records.add(record);
            }

        } catch (SQLException e) {

            System.out.println(
                    "[Database Error] Failed to retrieve issue records: "
                            + e.getMessage()
            );

        } finally {

            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }

        return records;
    }
}