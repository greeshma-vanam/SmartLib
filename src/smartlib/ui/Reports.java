package smartlib.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import smartlib.service.BookManager;
import smartlib.service.StudentManager;
import smartlib.service.IssueManager;

public class Reports {

    private static final BookManager bookManager =
            new BookManager();

    private static final StudentManager studentManager =
            new StudentManager();

    private static final IssueManager issueManager =
            new IssueManager(
                    bookManager,
                    studentManager
            );

    public static void show(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #F5F6F8;"
        );

        // =========================
        // TOP
        // =========================

        VBox top = new VBox(10);

        top.setPadding(
                new Insets(20, 30, 20, 30)
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

            String[] userData =
                    (String[]) stage.getUserData();

            if (userData != null) {

                Dashboard.show(
                        stage,
                        userData[0],
                        userData[1]
                );
            }
        });

        Label title =
                new Label("Reports");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label subtitle =
                new Label(
                        "Library statistics and reports"
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

        // =========================
        // REPORT CONTENT
        // =========================

        VBox content =
                new VBox(18);

        content.setPadding(
                new Insets(10, 30, 30, 30)
        );

        Label summaryTitle =
                new Label("Library Summary");

        summaryTitle.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        HBox cards =
                new HBox(15);

        Label totalBooks =
                createValueLabel("Total Books");

        Label availableBooks =
                createValueLabel("Available Books");

        Label issuedBooks =
                createValueLabel("Issued Books");

        Label totalStudents =
                createValueLabel("Total Students");

        cards.getChildren().addAll(
                createCard(totalBooks),
                createCard(availableBooks),
                createCard(issuedBooks),
                createCard(totalStudents)
        );

        Button refreshButton =
                new Button("Refresh Reports");

        refreshButton.setStyle(
                "-fx-background-color: #1F2937;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 9px 18px;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
        );

        refreshButton.setOnAction(
                event -> updateReports(
                        totalBooks,
                        availableBooks,
                        issuedBooks,
                        totalStudents
                )
        );

        Label ruleTitle =
                new Label("Library Rules");

        ruleTitle.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label rules =
                new Label(
                        "• Maximum issue period: 7 days\n" +
                        "• Late return fine: ₹5 per day\n" +
                        "• Book becomes available after return\n" +
                        "• Each issue is stored in the database"
                );

        rules.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475467;" +
                "-fx-line-spacing: 8px;"
        );

        content.getChildren().addAll(
                summaryTitle,
                cards,
                refreshButton,
                ruleTitle,
                rules
        );

        root.setCenter(content);

        // Initial values
        updateReports(
                totalBooks,
                availableBooks,
                issuedBooks,
                totalStudents
        );

        Scene scene =
                new Scene(
                        root,
                        1050,
                        650
                );

        stage.setTitle(
                "SMARTLIB - Reports"
        );

        stage.setScene(scene);

        stage.show();
    }

    // =========================
    // UPDATE REPORTS
    // =========================

    private static void updateReports(
            Label totalBooks,
            Label availableBooks,
            Label issuedBooks,
            Label totalStudents
    ) {

        int total =
                bookManager.getTotalBooksCount();

        int available =
                bookManager.getAvailableBooksCount();

        int issued =
                bookManager.getIssuedBooksCount();

        int students =
                studentManager.getTotalStudentsCount();

        totalBooks.setText(
                "Total Books\n" + total
        );

        availableBooks.setText(
                "Available Books\n" + available
        );

        issuedBooks.setText(
                "Issued Books\n" + issued
        );

        totalStudents.setText(
                "Total Students\n" + students
        );
    }

    // =========================
    // VALUE LABEL
    // =========================

    private static Label createValueLabel(
            String title
    ) {

        Label label =
                new Label(title + "\n0");

        label.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;" +
                "-fx-padding: 20px;"
        );

        return label;
    }

    // =========================
    // CARD
    // =========================

    private static VBox createCard(
            Label label
    ) {

        VBox card =
                new VBox(label);

        card.setPrefWidth(210);
        card.setPrefHeight(110);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #D0D5DD;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4px;" +
                "-fx-background-radius: 4px;"
        );

        return card;
    }
}