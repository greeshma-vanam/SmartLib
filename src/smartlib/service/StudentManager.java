package smartlib.service;

import smartlib.database.DatabaseConnection;
import smartlib.model.Student;

import java.sql.*;
import java.util.ArrayList;

public class StudentManager {

    public StudentManager() {
        ensureDefaultStudents();
    }

    private void ensureDefaultStudents() {
        if (getTotalStudentsCount() == 0) {
            addStudent("S001", "Rahul Sharma", "CSE-AIML", "3rd", "rahul@smartlib.com");
            addStudent("S002", "Priya Verma", "CSE", "3rd", "priya@smartlib.com");
        }
    }

    public boolean addStudent(String studentId, String name, String department, String year, String email) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "INSERT INTO students(student_id, name, department, year, email) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, name);
            pstmt.setString(3, department);
            pstmt.setString(4, year);
            pstmt.setString(5, email);

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

    public void viewAllStudents() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("[Database Error] Cannot connect to database.");
            return;
        }

        String sql = "SELECT * FROM students";
        ArrayList<Student> students = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String name = rs.getString("name");
                String department = rs.getString("department");
                String year = rs.getString("year");
                String email = rs.getString("email");

                students.add(new Student(studentId, name, department, year, email));
            }

        } catch (SQLException e) {
            System.out.println("[Database Error] Failed to retrieve students: " + e.getMessage());
            return;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close exception
            }
        }

        if (students.isEmpty()) {
            System.out.println("\n[Info] No students registered in the system.");
            return;
        }

        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("                                ALL STUDENTS LIST");
        System.out.println("----------------------------------------------------------------------------------");
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    public void searchStudent(String query) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("[Database Error] Cannot connect to database.");
            return;
        }

        String sql = "SELECT * FROM students WHERE student_id = ? OR LOWER(name) LIKE ?";
        ArrayList<Student> students = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, query);
            pstmt.setString(2, "%" + query.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    String year = rs.getString("year");
                    String email = rs.getString("email");

                    students.add(new Student(studentId, name, department, year, email));
                }
            }

        } catch (SQLException e) {
            System.out.println("[Database Error] Failed to search students: " + e.getMessage());
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
        if (students.isEmpty()) {
            System.out.println("[Info] No students match your search query: " + query);
        } else {
            for (Student s : students) {
                System.out.println(s);
            }
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    public boolean updateStudent(String studentId, String newName, String newDept, String newYear, String newEmail) {
        Student existing = findStudentById(studentId);
        if (existing == null) {
            return false;
        }

        String nameToSave = newName.isEmpty() ? existing.getName() : newName;
        String deptToSave = newDept.isEmpty() ? existing.getDepartment() : newDept;
        String yearToSave = newYear.isEmpty() ? existing.getYear() : newYear;
        String emailToSave = newEmail.isEmpty() ? existing.getEmail() : newEmail;

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "UPDATE students SET name = ?, department = ?, year = ?, email = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nameToSave);
            pstmt.setString(2, deptToSave);
            pstmt.setString(3, yearToSave);
            pstmt.setString(4, emailToSave);
            pstmt.setString(5, studentId);

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

    public boolean deleteStudent(String studentId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
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

    public Student findStudentById(String studentId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("student_id");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    String year = rs.getString("year");
                    String email = rs.getString("email");

                    return new Student(id, name, department, year, email);
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

    public int getTotalStudentsCount() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM students";
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