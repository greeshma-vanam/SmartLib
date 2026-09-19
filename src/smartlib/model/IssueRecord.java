package smartlib.model;

import java.time.LocalDate;

public class IssueRecord {
    private String bookId;
    private String studentId;
    private String status; // e.g., "ISSUED"
    private LocalDate issueDate;

    public IssueRecord(String bookId, String studentId, String status) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.status = status;
        this.issueDate = LocalDate.now(); // Automatically capture current date on issue
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return String.format("Book ID: %-6s | Student ID: %-6s | Issue Date: %-10s | Status: %s", 
                bookId, studentId, issueDate.toString(), status);
    }
}