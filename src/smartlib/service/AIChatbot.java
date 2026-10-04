
package smartlib.service;

import smartlib.model.Book;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class AIChatbot {

    private Scanner scanner;
    private BookManager bookManager;
    private HttpClient httpClient;

    public AIChatbot(Scanner scanner, BookManager bookManager) {
        this.scanner = scanner;
        this.bookManager = bookManager;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void startChat() {

        String apiKey = System.getenv("OPENROUTER_API_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println(
                    "AI Chatbot Error: OPENROUTER_API_KEY is not set."
            );
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("              SMARTLIB - AI CHATBOT");
        System.out.println("==================================================");
        System.out.println("Hello! You can ask questions about SmartLib.");
        System.out.println("Type 'exit' to quit the chatbot.\n");

        while (true) {

            System.out.print("You: ");
            String query = scanner.nextLine().trim();

            if (query.equalsIgnoreCase("exit")) {
                System.out.println(
                        "AI Chatbot: Goodbye! Have a great day.\n"
                );
                break;
            }

            if (query.isEmpty()) {
                continue;
            }

            try {

                String libraryData = buildLibraryData();

                String prompt =
                        "You are the AI assistant for SmartLib, "
                        + "a Java-based Library Management System.\n\n"

                        + "SmartLib rules and procedures:\n"
                        + "- Books can be issued by selecting Issue Book "
                        + "from the main menu.\n"
                        + "- To issue a book, the user provides a Book ID "
                        + "and Student ID.\n"
                        + "- A book can only be issued if it exists and "
                        + "is currently available.\n"
                        + "- Books can be returned using the Return Book "
                        + "option.\n"
                        + "- The standard loan period is 7 days.\n"
                        + "- The fine is Rs.5 for each day after the "
                        + "7-day loan period.\n"
                        + "- Students can search books, view reports, "
                        + "view the dashboard, and use the AI chatbot.\n"
                        + "- Librarians can manage books and students, "
                        + "issue and return books, search, view reports, "
                        + "use the dashboard, and use the AI chatbot.\n"
                        + "- Admin users have full access to SmartLib.\n\n"

                        + "Current library book information:\n"
                        + libraryData

                        + "\n\nUser question: "
                        + query

                        + "\n\nAnswer clearly and simply. "
                        + "Use the current library information when the "
                        + "question is about books. "
                        + "Use the SmartLib rules when the question is "
                        + "about procedures. "
                        + "If the requested information is not available, "
                        + "say that you do not have that information. "
                        + "Do not invent library data.";

                long start = System.nanoTime();

                String answer = callOpenRouter(apiKey, prompt);

                long end = System.nanoTime();

                long timeMs = (end - start) / 1_000_000;

                System.out.println(
                        "AI Chatbot: " + answer
                );

                System.out.println(
                        "[OpenRouter Generation: "
                        + timeMs
                        + " ms]"
                );

            } catch (Exception e) {

                System.out.println(
                        "AI Chatbot Error: "
                        + e.getMessage()
                );
            }

            System.out.println();
        }
    }

    private String buildLibraryData() {

        StringBuilder data = new StringBuilder();

        ArrayList<Book> books =
                bookManager.getAllBooks();

        if (books.isEmpty()) {
            return "There are currently no books in the library.";
        }

        for (Book book : books) {

            data.append("Book ID: ")
                    .append(book.getBookId())
                    .append(", Title: ")
                    .append(book.getTitle())
                    .append(", Author: ")
                    .append(book.getAuthor())
                    .append(", Category: ")
                    .append(book.getCategory())
                    .append(", Available: ")
                    .append(book.isAvailable())
                    .append("\n");
        }

        return data.toString();
    }

    private String callOpenRouter(
            String apiKey,
            String prompt
    ) throws Exception {

        String escapedPrompt =
                escapeJson(prompt);

        String jsonBody =
                "{"
                + "\"model\":\"openrouter/free\","
                + "\"messages\":["
                + "{"
                + "\"role\":\"user\","
                + "\"content\":\""
                + escapedPrompt
                + "\""
                + "}"
                + "],"
                + "\"temperature\":0.3,"
                + "\"max_tokens\":300"
                + "}";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                "https://openrouter.ai/api/v1/chat/completions"
                        ))
                        .header(
                                "Authorization",
                                "Bearer " + apiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .header(
                                "HTTP-Referer",
                                "https://smartlib.local"
                        )
                        .header(
                                "X-Title",
                                "SMARTLIB AI Chatbot"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(jsonBody)
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new RuntimeException(
                    "HTTP "
                    + response.statusCode()
                    + ": "
                    + response.body()
            );
        }

        return extractResponseText(
                response.body()
        );
    }

    private String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String extractResponseText(String json) {

        String marker = "\"content\":\"";

        int start = json.indexOf(marker);

        if (start == -1) {
            return "No response generated.";
        }

        start += marker.length();

        StringBuilder result =
                new StringBuilder();

        boolean escaped = false;

        for (int i = start;
             i < json.length();
             i++) {

            char c = json.charAt(i);

            if (escaped) {

                switch (c) {

                    case 'n':
                        result.append('\n');
                        break;

                    case 'r':
                        result.append('\r');
                        break;

                    case 't':
                        result.append('\t');
                        break;

                    case '"':
                        result.append('"');
                        break;

                    case '\\':
                        result.append('\\');
                        break;

                    default:
                        result.append(c);
                }

                escaped = false;

            } else if (c == '\\') {

                escaped = true;

            } else if (c == '"') {

                break;

            } else {

                result.append(c);
            }
        }

        return result.toString().trim();
    }
}
