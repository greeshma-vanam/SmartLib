package smartlib.ui;

import smartlib.model.Student;
import smartlib.service.StudentManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class StudentMenu {
    private Scanner scanner;
    private StudentManager studentManager;

    public StudentMenu(Scanner scanner, StudentManager studentManager) {
        this.scanner = scanner;
        this.studentManager = studentManager; // Use shared instance
    }

    public void displayStudentMenu() {
        int choice = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("                STUDENT MANAGEMENT");
            System.out.println("==================================================");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Back to Main Menu");
            System.out.print("\nEnter your choice (1-6): ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("\n[Error] Invalid input! Please enter a number between 1 and 6.");
                scanner.nextLine();
                choice = 0;
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddStudent();
                    break;
                case 2:
                    studentManager.viewAllStudents();
                    break;
                case 3:
                    handleSearchStudent();
                    break;
                case 4:
                    handleUpdateStudent();
                    break;
                case 5:
                    handleDeleteStudent();
                    break;
                case 6:
                    System.out.println("\nReturning to Main Menu...");
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice.");
            }

            if (choice != 6) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 6);
    }

    private void handleAddStudent() {
        System.out.println("\n--- Add Student ---");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();
        System.out.print("Enter Year: ");
        String year = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        if (studentManager.addStudent(id, name, dept, year, email)) {
            System.out.println("\n[Success] Student added successfully!");
        } else {
            System.out.println("\n[Error] Student ID already exists!");
        }
    }

    private void handleSearchStudent() {
        System.out.println("\n--- Search Student ---");
        System.out.print("Enter Student ID or Name: ");
        String query = scanner.nextLine().trim();
        if (!query.isEmpty()) {
            studentManager.searchStudent(query);
        }
    }

    private void handleUpdateStudent() {
        System.out.println("\n--- Update Student ---");
        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine().trim();
        Student s = studentManager.findStudentById(id);
        if (s == null) {
            System.out.println("[Error] Student ID not found.");
            return;
        }
        System.out.print("Enter new Name (or press Enter to skip): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new Department (or press Enter to skip): ");
        String dept = scanner.nextLine().trim();
        System.out.print("Enter new Year (or press Enter to skip): ");
        String year = scanner.nextLine().trim();
        System.out.print("Enter new Email (or press Enter to skip): ");
        String email = scanner.nextLine().trim();

        if (studentManager.updateStudent(id, name, dept, year, email)) {
            System.out.println("\n[Success] Student updated successfully!");
        }
    }

    private void handleDeleteStudent() {
        System.out.println("\n--- Delete Student ---");
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();
        if (studentManager.deleteStudent(id)) {
            System.out.println("\n[Success] Student deleted successfully!");
        } else {
            System.out.println("[Error] Student ID not found.");
        }
    }
}