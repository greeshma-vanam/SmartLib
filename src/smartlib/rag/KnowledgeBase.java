package smartlib.rag;

public class KnowledgeBase {

    public static String getKnowledge() {
        return """
            SMARTLIB - SYSTEM KNOWLEDGE BASE & POLICIES
            
            1. Loan Period & Fines:
            - Standard loan period: 7 days.
            - Late fine: ₹5 per late day.
            
            2. Book Issue Procedure:
            - Access option 3 ('Issue Book') from the main menu (requires Librarian or Admin role).
            - Provide a valid Book ID and Student ID.
            - The system checks if the book exists, if the student exists, and if the book is currently available before completing the transaction.
            
            3. Book Return Procedure:
            - Access option 4 ('Return Book') from the main menu.
            - Provide the Book ID of the issued book.
            - The system calculates the days kept, determines any late days past the 7-day limit, computes the fine (₹5 per late day), and marks the book as available again.
            
            4. Role-Based Permissions:
            - Student: Can log into the system with student credentials for viewing, but restricted from management, issue/return, and reports modules.
            - Librarian: Has operational access to Book Management, Student Management, Issue Book, Return Book, Search, Reports, and Dashboard.
            - Admin: Full system access including user management, database controls, and all librarian features.
            """;
    }
}