package smartlib.service;

import smartlib.model.Book;
import smartlib.model.IssueRecord;
import smartlib.model.Student;

import java.util.ArrayList;

public class IssueManager {
    private ArrayList<IssueRecord> issueRecords;
    private BookManager bookManager;
    private StudentManager studentManager;

    public IssueManager(BookManager bookManager, StudentManager studentManager) {
        this.issueRecords = new ArrayList<>();
        this.bookManager = bookManager;
        this.studentManager = studentManager;
    }

    // Issue Book Logic
    public boolean issueBook(String bookId, String studentId) {
        // 1. Check if book exists
        Book book = bookManager.findBookById(bookId);
        if (book == null) {
            System.out.println("[Error] Book ID does not exist in the library.");
            return false;
        }

        // 2. Check if student exists
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            System.out.println("[Error] Student ID does not exist in the system.");
            return false;
        }

        // 3. Check if book is available
        if (!book.isAvailable()) {
            System.out.println("[Error] Book is already issued to another student.");
            return false;
        }

        // 4. Perform issue operation
        book.setAvailable(false);
        issueRecords.add(new IssueRecord(bookId, studentId, "ISSUED"));
        return true;
    }

    // Return Book Logic
    public boolean returnBook(String bookId) {
        // 1. Check if book exists
        Book book = bookManager.findBookById(bookId);
        if (book == null) {
            System.out.println("[Error] Book ID does not exist in the library.");
            return false;
        }

        // 2. Check if book is currently issued
        if (book.isAvailable()) {
            System.out.println("[Error] This book is already available in the library (not currently issued).");
            return false;
        }

        // 3. Perform return operation
        book.setAvailable(true);
        
        // Remove active issue record
        for (int i = 0; i < issueRecords.size(); i++) {
            if (issueRecords.get(i).getBookId().equalsIgnoreCase(bookId)) {
                issueRecords.remove(i);
                break;
            }
        }
        return true;
    }
}