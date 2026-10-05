package smartlib.service;

import smartlib.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    public static class Notification {

        private final int id;
        private final String title;
        private final String message;
        private final String type;
        private final boolean read;
        private final String createdAt;

        public Notification(
                int id,
                String title,
                String message,
                String type,
                boolean read,
                String createdAt
        ) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.type = type;
            this.read = read;
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }

        public boolean isRead() {
            return read;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    public static void createNotification(
            String recipientId,
            String recipientRole,
            String title,
            String message,
            String type
    ) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return;
        }

        String sql =
                "INSERT INTO notifications " +
                "(recipient_id, recipient_role, title, message, " +
                "notification_type, is_read, created_at) " +
                "VALUES (?, ?, ?, ?, ?, 0, ?)";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, recipientId);
            stmt.setString(2, recipientRole);
            stmt.setString(3, title);
            stmt.setString(4, message);
            stmt.setString(5, type);
            stmt.setString(
                    6,
                    LocalDateTime.now().toString()
            );

            stmt.executeUpdate();

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

    public static List<Notification> getNotifications(
            String recipientId,
            String role
    ) {

        List<Notification> notifications =
                new ArrayList<>();

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return notifications;
        }

        String sql =
                "SELECT * FROM notifications " +
                "WHERE recipient_id = ? " +
                "ORDER BY id DESC";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, recipientId);

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    notifications.add(
                            new Notification(
                                    rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getString("message"),
                                    rs.getString("notification_type"),
                                    rs.getInt("is_read") == 1,
                                    rs.getString("created_at")
                            )
                    );
                }
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

        return notifications;
    }

    public static int getUnreadCount(
            String recipientId
    ) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return 0;
        }

        String sql =
                "SELECT COUNT(*) FROM notifications " +
                "WHERE recipient_id = ? AND is_read = 0";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, recipientId);

            try (ResultSet rs =
                         stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
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

        return 0;
    }

    public static void markAsRead(int notificationId) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return;
        }

        String sql =
                "UPDATE notifications " +
                "SET is_read = 1 WHERE id = ?";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);
            stmt.executeUpdate();

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

    public static void markAllAsRead(
            String recipientId
    ) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return;
        }

        String sql =
                "UPDATE notifications " +
                "SET is_read = 1 " +
                "WHERE recipient_id = ?";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, recipientId);
            stmt.executeUpdate();

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

    /*
     * Creates return reminders for currently issued books.
     * This method is safe to call whenever the dashboard opens.
     */
    public static void generateReturnReminders(
            String studentId
    ) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return;
        }

        String sql =
                "SELECT ir.book_id, ir.issue_date, b.title " +
                "FROM issue_records ir " +
                "JOIN books b ON ir.book_id = b.book_id " +
                "WHERE ir.student_id = ? " +
                "AND ir.status = 'ISSUED'";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    String bookId =
                            rs.getString("book_id");

                    String title =
                            rs.getString("title");

                    LocalDate issueDate =
                            LocalDate.parse(
                                    rs.getString("issue_date")
                            );

                    LocalDate dueDate =
                            issueDate.plusDays(7);

                    long daysRemaining =
                            ChronoUnit.DAYS.between(
                                    LocalDate.now(),
                                    dueDate
                            );

                    if (daysRemaining > 2) {
                        continue;
                    }

                    String notificationType;

                    String message;

                    if (daysRemaining == 2) {

                        notificationType =
                                "RETURN_REMINDER";

                        message =
                                "Reminder: Your book \"" +
                                title +
                                "\" is due in 2 days.";

                    } else if (daysRemaining == 1) {

                        notificationType =
                                "RETURN_REMINDER";

                        message =
                                "Reminder: Your book \"" +
                                title +
                                "\" is due tomorrow.";

                    } else if (daysRemaining == 0) {

                        notificationType =
                                "DUE_TODAY";

                        message =
                                "Reminder: Please return \"" +
                                title +
                                "\" today.";

                    } else {

                        notificationType =
                                "OVERDUE";

                        message =
                                "Overdue: \"" +
                                title +
                                "\" is overdue. Fine may apply.";
                    }

                    if (!notificationExistsToday(
                            studentId,
                            bookId,
                            notificationType
                    )) {

                        createNotification(
                                studentId,
                                "STUDENT",
                                "Book Return Reminder",
                                message,
                                notificationType
                        );
                    }
                }
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

    private static boolean notificationExistsToday(
            String recipientId,
            String bookId,
            String type
    ) {

        Connection conn =
                DatabaseConnection.getConnection();

        if (conn == null) {
            return true;
        }

        String sql =
                "SELECT COUNT(*) " +
                "FROM notifications " +
                "WHERE recipient_id = ? " +
                "AND notification_type = ? " +
                "AND message LIKE ? " +
                "AND date(created_at) = date('now')";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, recipientId);
            stmt.setString(2, type);
            stmt.setString(
                    3,
                    "%" + bookId + "%"
            );

            try (ResultSet rs =
                         stmt.executeQuery()) {

                return rs.next() &&
                       rs.getInt(1) > 0;
            }

        } catch (SQLException e) {

            return true;

        } finally {

            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}