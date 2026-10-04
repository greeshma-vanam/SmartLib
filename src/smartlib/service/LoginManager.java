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

        try {

            // Reset ONLY the Admin account
            String deleteAdmin =
                    "DELETE FROM users WHERE username = ?";

            try (PreparedStatement pstmt =
                         conn.prepareStatement(deleteAdmin)) {

                pstmt.setString(1, "admin");
                pstmt.executeUpdate();
            }

            // Create Admin account with correct credentials
            String insertAdmin =
                    "INSERT INTO users (username, password, role) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement pstmt =
                         conn.prepareStatement(insertAdmin)) {

                pstmt.setString(1, "admin");
                pstmt.setString(2, "admin123");
                pstmt.setString(3, "ADMIN");
                pstmt.executeUpdate();
            }

            // Create Librarian account if it does not exist
            String insertUser =
                    "INSERT OR IGNORE INTO users " +
                    "(username, password, role) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt =
                         conn.prepareStatement(insertUser)) {

                pstmt.setString(1, "librarian");
                pstmt.setString(2, "lib123");
                pstmt.setString(3, "LIBRARIAN");
                pstmt.executeUpdate();

                pstmt.setString(1, "student");
                pstmt.setString(2, "student123");
                pstmt.setString(3, "STUDENT");
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println(
                    "[Database Error] " + e.getMessage()
            );

        } finally {

            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close error
            }
        }
    }

    public User login(String username, String password) {

        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            return null;
        }

        String sql =
                "SELECT * FROM users " +
                "WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password.trim());

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    String dbUsername =
                            rs.getString("username");

                    String dbPassword =
                            rs.getString("password");

                    String dbRole =
                            rs.getString("role");

                    return new User(
                            dbUsername,
                            dbPassword,
                            dbRole
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "[Login Error] " + e.getMessage()
            );

        } finally {

            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close error
            }
        }

        return null;
    }
}
