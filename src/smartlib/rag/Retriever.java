package smartlib.rag;

import java.util.ArrayList;
import java.util.List;

public class Retriever {

    private final List<String> chunks = List.of(
        "Standard loan period: 7 days. Late fine: ₹5 per late day.",
        "Book issue procedure: Access option 3 ('Issue Book') from the main menu (requires Librarian or Admin role). Provide a valid Book ID and Student ID. The system checks if the book exists, if the student exists, and if the book is currently available before completing the transaction.",
        "Book return procedure: Access option 4 ('Return Book') from the main menu. Provide the Book ID of the issued book. The system calculates the days kept, determines any late days past the 7-day limit, computes the fine (₹5 per late day), and marks the book as available again.",
        "Role-Based Permissions - Student: Can log into the system with student credentials for viewing, but restricted from management, issue/return, and reports modules.",
        "Role-Based Permissions - Librarian: Has operational access to Book Management, Student Management, Issue Book, Return Book, Search, Reports, and Dashboard.",
        "Role-Based Permissions - Admin: Full system access including user management, database controls, and all librarian features."
    );

    public String retrieve(String query) {

        if (query == null || query.trim().isEmpty()) {
            return "No relevant information found.";
        }

        String lowerQuery = query.toLowerCase();

        List<ChunkScore> scores = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {

            String lowerChunk = chunks.get(i).toLowerCase();

            int score = 0;

            String[] words = lowerQuery.split("\\s+");

            for (String word : words) {

                word = word.replaceAll("[^a-z0-9]", "");

                if (word.length() < 3) {
                    continue;
                }

                if (lowerChunk.contains(word)) {
                    score++;
                }
            }

            // Give extra importance to important SmartLib keywords
            if (lowerQuery.contains("issue") &&
                    lowerChunk.contains("issue")) {
                score += 3;
            }

            if (lowerQuery.contains("return") &&
                    lowerChunk.contains("return")) {
                score += 3;
            }

            if ((lowerQuery.contains("fine") ||
                 lowerQuery.contains("loan") ||
                 lowerQuery.contains("days")) &&
                (lowerChunk.contains("fine") ||
                 lowerChunk.contains("loan") ||
                 lowerChunk.contains("days"))) {
                score += 3;
            }

            if (lowerQuery.contains("student") &&
                    lowerChunk.contains("student")) {
                score += 2;
            }

            if (lowerQuery.contains("librarian") &&
                    lowerChunk.contains("librarian")) {
                score += 2;
            }

            if (lowerQuery.contains("admin") &&
                    lowerChunk.contains("admin")) {
                score += 2;
            }

            scores.add(new ChunkScore(i, score));
        }

        scores.sort((a, b) ->
                Integer.compare(b.score, a.score));

        StringBuilder result = new StringBuilder();

        int count = 0;

        for (ChunkScore cs : scores) {

            if (cs.score > 0 && count < 3) {

                if (result.length() > 0) {
                    result.append("\n\n");
                }

                result.append(chunks.get(cs.index));
                count++;
            }
        }

        // If no SmartLib keywords matched, return all knowledge
        // so Gemini can still answer general questions naturally.
        if (result.length() == 0) {

            for (String chunk : chunks) {

                if (result.length() > 0) {
                    result.append("\n\n");
                }

                result.append(chunk);
            }
        }

        return result.toString();
    }

    private static class ChunkScore {

        int index;
        int score;

        ChunkScore(int index, int score) {
            this.index = index;
            this.score = score;
        }
    }
}