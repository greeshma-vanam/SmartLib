package smartlib.rag;

import smartlib.model.Book;
import smartlib.service.BookManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class RAGChatbot {

private Scanner scanner;
private Retriever retriever;
private BookManager bookManager;
private HttpClient httpClient;

public RAGChatbot(Scanner scanner) {
    this.scanner = scanner;
    this.retriever = new Retriever();
    this.bookManager = new BookManager();
    this.httpClient = HttpClient.newHttpClient();
}

public void startChat() {

    String apiKey = System.getenv("OPENROUTER_API_KEY");

    if (apiKey == null || apiKey.trim().isEmpty()) {
        System.out.println(
                "[Error] OPENROUTER_API_KEY environment variable is not set."
        );
        return;
    }

    System.out.println("\n==================================================");
    System.out.println("            SMARTLIB - AI RAG CHATBOT");
    System.out.println("==================================================");
    System.out.println(
            "Hello! Ask me about SmartLib, books, rules, or general topics."
    );
    System.out.println("Type 'exit' to quit.\n");

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

        long totalStart = System.nanoTime();

        long retrievalStart = System.nanoTime();

        String retrievedContext = retriever.retrieve(query);

        long retrievalEnd = System.nanoTime();

        long retrievalTimeMs =
                (retrievalEnd - retrievalStart) / 1_000_000;

        long databaseStart = System.nanoTime();

        String liveBookData = getLiveBookData();

        long databaseEnd = System.nanoTime();

        long databaseTimeMs =
                (databaseEnd - databaseStart) / 1_000_000;

        String prompt = """
                You are the AI assistant for SMARTLIB
                (Smart Library Management System).

                You have two information sources.

                SOURCE 1 - AUTHORITATIVE RAG KNOWLEDGE:
                %s

                SOURCE 2 - LIVE LIBRARY DATABASE:
                %s

                USER QUESTION:
                %s

                INSTRUCTIONS:
                - Use RAG knowledge for SmartLib rules,
                  policies, procedures, permissions, fines,
                  and library operations.
                - Use the live database for books,
                  availability, authors, categories,
                  issued books, and inventory.
                - If both sources are relevant, combine them.
                - Never invent library database facts.
                - If information is unavailable, say so clearly.
                - For general questions, answer normally.
                - Keep answers concise and helpful.
                - Do not show analysis or reasoning.
                - Do not show planning.
                - Do not show source-selection steps.
                - Do not include drafts or internal notes.
                - Return only the final answer.
                """.formatted(
                retrievedContext,
                liveBookData,
                query
        );

        try {

            long aiStart = System.nanoTime();

            String answer = callOpenRouter(apiKey, prompt);

            long aiEnd = System.nanoTime();

            long aiTimeMs =
                    (aiEnd - aiStart) / 1_000_000;

            System.out.println(
                    "AI Chatbot: " + answer
            );

            long totalEnd = System.nanoTime();

            long totalTimeMs =
                    (totalEnd - totalStart) / 1_000_000;

            System.out.println(
                    "[RAG Timing] Retrieval: "
                    + retrievalTimeMs
                    + " ms"
            );

            System.out.println(
                    "[Database Timing] Live Book Data: "
                    + databaseTimeMs
                    + " ms"
            );

            System.out.println(
                    "[RAG Timing] OpenRouter Generation: "
                    + aiTimeMs
                    + " ms"
            );

            System.out.println(
                    "[RAG Timing] Total: "
                    + totalTimeMs
                    + " ms"
            );

        } catch (Exception e) {

            System.out.println(
                    "[Error] OpenRouter API error: "
                    + e.getMessage()
            );
        }

        System.out.println();
    }
}

private String getLiveBookData() {

    ArrayList<Book> books = bookManager.getAllBooks();

    if (books.isEmpty()) {
        return "No books are currently available in the database.";
    }

    StringBuilder data = new StringBuilder();

    data.append("Current books in the SmartLib database:\n");

    for (Book book : books) {

        data.append("Book ID: ")
                .append(book.getBookId())
                .append(" | Title: ")
                .append(book.getTitle())
                .append(" | Author: ")
                .append(book.getAuthor())
                .append(" | Category: ")
                .append(book.getCategory())
                .append(" | Availability: ")
                .append(
                        book.isAvailable()
                                ? "Available"
                                : "Issued"
                )
                .append("\n");
    }

    return data.toString();
}

private String callOpenRouter(
        String apiKey,
        String prompt
) throws Exception {

    String escapedPrompt = escapeJson(prompt);

    String jsonBody =
            "{"
            + "\"model\":\"meta-llama/llama-3.3-8b-instruct:free\","
            + "\"messages\":["
            + "{"
            + "\"role\":\"user\","
            + "\"content\":\"" + escapedPrompt + "\""
            + "}"
            + "],"
            + "\"temperature\":0.3,"
            + "\"max_tokens\":500"
            + "}";

    HttpRequest request = HttpRequest.newBuilder()
            .uri(
                    URI.create(
                            "https://openrouter.ai/api/v1/chat/completions"
                    )
            )
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
                    HttpRequest.BodyPublishers.ofString(jsonBody)
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

    String answer = extractResponseText(response.body());

    if (answer.isEmpty()) {
        throw new RuntimeException(
                "OpenRouter returned an empty response: "
                + response.body()
        );
    }

    return answer;
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

        marker = "\"content\": \"";

        start = json.indexOf(marker);
    }

    if (start == -1) {
        return "";
    }

    start += marker.length();

    StringBuilder result = new StringBuilder();

    boolean escaped = false;

    for (int i = start; i < json.length(); i++) {

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

                case '/':
                    result.append('/');
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
