public class SmartLib {

    public static void main(String[] args) {

        Book book1 = new Book();

        book1.bookId = 101;
        book1.title = "Java Programming";
        book1.author = "James Gosling";
        book1.available = true;

        System.out.println("Book ID: " + book1.bookId);
        System.out.println("Title: " + book1.title);
        System.out.println("Author: " + book1.author);
        System.out.println("Available: " + book1.available);
    }
}