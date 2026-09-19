package smartlib.model;

public class Student {
    private String studentId;
    private String name;
    private String department;
    private String year;
    private String email;

    // Constructor
    public Student(String studentId, String name, String department, String year, String email) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.email = email;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // toString method for neat formatting
    @Override
    public String toString() {
        return String.format("ID: %-6s | Name: %-18s | Dept: %-8s | Year: %-5s | Email: %s",
                studentId, name, department, year, email);
    }
}