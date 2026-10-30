package smartlib.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DATABASE_FOLDER =
            System.getProperty("user.home") + File.separator + "SMARTLIB";

    private static final String DATABASE_PATH =
            DATABASE_FOLDER + File.separator + "smartlib.db";

    private static final String URL =
            "jdbc:sqlite:" + DATABASE_PATH;

    public static Connection getConnection() {

        try {

            File folder = new File(DATABASE_FOLDER);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            return DriverManager.getConnection(URL);

        } catch (SQLException e) {

            System.out.println(
                    "[Database Error] Failed to connect to SQLite database: "
                    + e.getMessage()
            );

            return null;
        }
    }

    public static String getDatabasePath() {
        return DATABASE_PATH;
    }
}