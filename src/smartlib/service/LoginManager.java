package smartlib.service;

import smartlib.database.DatabaseConnection;
import smartlib.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManager {

    public LoginManager() {
        ensureDefaultUsers();
    }

    private void ensureDefaultUsers() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return;
        }

        String sql = "INSERT OR IGNORE INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Admin account
            pstmt.setString(1, "admin");
            pstmt.setString(2, "admin123");
            pstmt.setString(3, "ADMIN");
            pstmt.executeUpdate();

            // Librarian account
            pstmt.setString(1, "librarian");
            pstmt.setString(2, "lib123");
            pstmt.setString(3, "LIBRARIAN");
            pstmt.executeUpdate();

            // Student account
            pstmt.setString(1, "student");
            pstmt.setString(2, "student123");
            pstmt.setString(3, "STUDENT");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Ignore seeding exception or log if needed
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }
    }

    public User login(String username, String password) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbUsername = rs.getString("username");
                    String dbPassword = rs.getString("password");
                    String dbRole = rs.getString("role");

                    return new User(dbUsername, dbPassword, dbRole);
                }
            }
        } catch (SQLException e) {
            // Return null on SQL exception
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }

        return null;
    }
}