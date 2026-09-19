package smartlib.service;

import smartlib.model.Student;
import java.util.ArrayList;

public class StudentManager {
    private ArrayList<Student> students;

    public StudentManager() {
        students = new ArrayList<>();
        // Pre-loaded sample students for easy testing
        students.add(new Student("S001", "Rahul Sharma", "CSE-AIML", "3rd", "rahul@smartlib.com"));
        students.add(new Student("S002", "Priya Verma", "CSE", "3rd", "priya@smartlib.com"));
    }

    // Add a new student
    public boolean addStudent(String studentId, String name, String department, String year, String email) {
        // Check for duplicate Student ID
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                return false;
            }
        }
        students.add(new Student(studentId, name, department, year, email));
        return true;
    }

    // View all students
    public void viewAllStudents() {
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

    // Search student by ID or Name
    public void searchStudent(String query) {
        boolean found = false;
        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("                                  SEARCH RESULTS");
        System.out.println("----------------------------------------------------------------------------------");
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(query) || s.getName().toLowerCase().contains(query.toLowerCase())) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) {
            System.out.println("[Info] No students match your search query: " + query);
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    // Update student details
    public boolean updateStudent(String studentId, String newName, String newDept, String newYear, String newEmail) {
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                if (!newName.isEmpty()) s.setName(newName);
                if (!newDept.isEmpty()) s.setDepartment(newDept);
                if (!newYear.isEmpty()) s.setYear(newYear);
                if (!newEmail.isEmpty()) s.setEmail(newEmail);
                return true;
            }
        }
        return false;
    }

    // Delete a student by ID
    public boolean deleteStudent(String studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equalsIgnoreCase(studentId)) {
                students.remove(i);
                return true;
            }
        }
        return false;
    }

    // Find student object by ID
    public Student findStudentById(String studentId) {
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                return s;
            }
        }
        return null;
    }
}