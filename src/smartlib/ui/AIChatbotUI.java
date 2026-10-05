package smartlib.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import smartlib.rag.RAGChatbot;

public class AIChatbotUI {

    public static void show(Stage stage) {

        String[] userData =
                (String[]) stage.getUserData();

        String username = "User";
        String role = "STUDENT";

        if (userData != null && userData.length >= 2) {
            username = userData[0];
            role = userData[1];
        }

        final String currentUsername = username;
        final String currentRole = role;

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #F5F6F8;"
        );

        // =====================================
        // TOP
        // =====================================

        VBox top = new VBox(8);

        top.setPadding(
                new Insets(20, 30, 15, 30)
        );

        Button backButton =
                new Button("← Back to Dashboard");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #344054;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(event -> {

            Dashboard.show(
                    stage,
                    currentUsername,
                    currentRole
            );
        });

        Label title =
                new Label("AI Chatbot");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label subtitle =
                new Label(
                        "Ask questions about SmartLib, books, rules and library operations."
                );

        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #667085;"
        );

        top.getChildren().addAll(
                backButton,
                title,
                subtitle
        );

        root.setTop(top);

        // =====================================
        // CHAT AREA
        // =====================================

        VBox chatArea =
                new VBox(12);

        chatArea.setPadding(
                new Insets(20, 30, 10, 30)
        );

        ScrollPane scrollPane =
                new ScrollPane(chatArea);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setStyle(
                "-fx-background: #FFFFFF;" +
                "-fx-border-color: #D0D5DD;"
        );

        root.setCenter(scrollPane);

        // =====================================
        // WELCOME MESSAGE
        // =====================================

        addMessage(
                chatArea,
                "AI",
                "Hello! I am the SMARTLIB AI Assistant. " +
                "You can ask me about books, availability, " +
                "library rules, fines and other SmartLib questions."
        );

        // =====================================
        // INPUT AREA
        // =====================================

        HBox inputArea =
                new HBox(10);

        inputArea.setPadding(
                new Insets(15, 30, 20, 30)
        );

        TextField inputField =
                new TextField();

        inputField.setPromptText(
                "Ask something about SmartLib..."
        );

        inputField.setPrefHeight(42);

        HBox.setHgrow(
                inputField,
                Priority.ALWAYS
        );

        Button sendButton =
                new Button("Send");

        sendButton.setPrefHeight(42);
        sendButton.setPrefWidth(90);

        sendButton.setStyle(
                "-fx-background-color: #1F2937;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
        );

        inputArea.getChildren().addAll(
                inputField,
                sendButton
        );

        root.setBottom(inputArea);

        // =====================================
        // RAG CHATBOT
        // =====================================

        RAGChatbot chatbot =
                new RAGChatbot(null, currentRole);

        // =====================================
        // SEND ACTION
        // =====================================

        Runnable sendMessage = () -> {

            String question =
                    inputField.getText().trim();

            if (question.isEmpty()) {
                return;
            }

            addMessage(
                    chatArea,
                    "You",
                    question
            );

            inputField.clear();

            sendButton.setDisable(true);
            inputField.setDisable(true);

            Label thinking =
                    new Label("AI is thinking...");

            thinking.setStyle(
                    "-fx-text-fill: #667085;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 5px;"
            );

            chatArea.getChildren().add(
                    thinking
            );

            Thread thread =
                    new Thread(() -> {

                        String answer =
                                chatbot.ask(question);

                        Platform.runLater(
                                () -> {

                                    chatArea.getChildren()
                                            .remove(thinking);

                                    addMessage(
                                            chatArea,
                                            "AI",
                                            answer
                                    );

                                    sendButton.setDisable(false);
                                    inputField.setDisable(false);

                                    inputField.requestFocus();

                                    scrollPane.setVvalue(1.0);
                                }
                        );
                    });

            thread.setDaemon(true);
            thread.start();
        };

        sendButton.setOnAction(
                event -> sendMessage.run()
        );

        inputField.setOnAction(
                event -> sendMessage.run()
        );

        Scene scene =
                new Scene(
                        root,
                        1100,
                        700
                );

        stage.setTitle(
                "SMARTLIB - AI Chatbot"
        );

        stage.setScene(scene);

        stage.show();
    }

    // =====================================
    // ADD CHAT MESSAGE
    // =====================================

    private static void addMessage(
            VBox chatArea,
            String sender,
            String message
    ) {

        VBox messageBox =
                new VBox(4);

        messageBox.setPadding(
                new Insets(10, 14, 10, 14)
        );

        messageBox.setMaxWidth(
                850
        );

        Label senderLabel =
                new Label(sender);

        senderLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #344054;"
        );

        Label messageLabel =
                new Label(message);

        messageLabel.setWrapText(true);

        messageLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #172033;"
        );

        messageBox.getChildren().addAll(
                senderLabel,
                messageLabel
        );

        if (sender.equals("You")) {

            messageBox.setStyle(
                    "-fx-background-color: #E9EEF5;" +
                    "-fx-border-color: #D0D5DD;" +
                    "-fx-border-radius: 4px;" +
                    "-fx-background-radius: 4px;"
            );

            messageBox.setAlignment(
                    Pos.CENTER_RIGHT
            );

        } else {

            messageBox.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #D0D5DD;" +
                    "-fx-border-radius: 4px;" +
                    "-fx-background-radius: 4px;"
            );
        }

        chatArea.getChildren().add(
                messageBox
        );
    }
}