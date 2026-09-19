package smartlib.service;

import smartlib.model.Book;
import smartlib.model.IssueRecord;
import smartlib.model.Student;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    public boolean issueBook(String bookId, String studentId) {
        Book book = bookManager.findBookById(bookId);
        if (book == null) {
            System.out.println("[Error] Book ID does not exist in the library.");
            return false;
        }

        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            System.out.println("[Error] Student ID does not exist in the system.");
            return false;
        }

        if (!book.isAvailable()) {
            System.out.println("[Error] Book is already issued to another student.");
            return false;
        }

        book.setAvailable(false);
        IssueRecord record = new IssueRecord(bookId, studentId, "ISSUED");
        issueRecords.add(record);
        System.out.println("[Info] Book issued successfully on: " + record.getIssueDate());
        return true;
    }

    public boolean returnBook(String bookId) {
        Book book = bookManager.findBookById(bookId);
        if (book == null) {
            System.out.println("[Error] Book ID does not exist in the library.");
            return false;
        }

        if (book.isAvailable()) {
            System.out.println("[Error] This book is already available in the library (not currently issued).");
            return false;
        }

        IssueRecord targetRecord = null;
        int targetIndex = -1;
        for (int i = 0; i < issueRecords.size(); i++) {
            if (issueRecords.get(i).getBookId().equalsIgnoreCase(bookId)) {
                targetRecord = issueRecords.get(i);
                targetIndex = i;
                break;
            }
        }

        if (targetRecord == null) {
            System.out.println("[Error] No active issue record found for this Book ID.");
            return false;
        }

        LocalDate issueDate = targetRecord.getIssueDate();
        LocalDate returnDate = LocalDate.now();

        long daysKept = ChronoUnit.DAYS.between(issueDate, returnDate);
        if (daysKept < 0) {
            daysKept = 0;
        }

        long lateDays = daysKept > 7 ? daysKept - 7 : 0;
        long fineAmount = lateDays * 5;

        book.setAvailable(true);
        issueRecords.remove(targetIndex);

        System.out.println("\n--------------------------------------------------");
        System.out.println("                 RETURN & FINE SUMMARY");
        System.out.println("--------------------------------------------------");
        System.out.println("Issue Date     : " + issueDate);
        System.out.println("Return Date    : " + returnDate);
        System.out.println("Days Kept      : " + daysKept);
        System.out.println("Late Days      : " + lateDays);
        if (fineAmount > 0) {
            System.out.println("Fine Amount    : Rs." + fineAmount);
        } else {
            System.out.println("Fine Amount    : Rs.0");
        }
        System.out.println("--------------------------------------------------");

        return true;
    }

    // Getter for Reports Module
    public ArrayList<IssueRecord> getIssueRecords() {
        return issueRecords;
    }
}