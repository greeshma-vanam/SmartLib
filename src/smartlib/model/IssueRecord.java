package smartlib.model;

public class IssueRecord {
    private String bookId;
    private String studentId;
    private String status; // e.g., "ISSUED"

    public IssueRecord(String bookId, String studentId, String status) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.status = status;
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

    @Override
    public String toString() {
        return String.format("Book ID: %-6s | Student ID: %-6s | Status: %s", bookId, studentId, status);
    }
}