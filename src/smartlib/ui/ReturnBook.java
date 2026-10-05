package smartlib.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import smartlib.model.IssueRecord;
import smartlib.service.BookManager;
import smartlib.service.IssueManager;
import smartlib.service.StudentManager;

public class ReturnBook {

    private static final BookManager bookManager =
            new BookManager();

    private static final StudentManager studentManager =
            new StudentManager();

    private static final IssueManager issueManager =
            new IssueManager(bookManager, studentManager);

    public static void show(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #F5F6F8;"
        );

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
        "-fx-padding: 0;"
);

backButton.setOnAction(event -> {

    String[] userData =
            (String[]) stage.getUserData();

    Dashboard.show(
            stage,
            userData[0],
            userData[1]
    );
});
        Label title = new Label("Return Book");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label subtitle = new Label(
                "Return an issued book to the library"
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

        GridPane form = new GridPane();

        form.setHgap(15);
        form.setVgap(12);

        form.setPadding(
                new Insets(10, 30, 20, 30)
        );

        Label bookLabel = new Label("Book ID:");

        TextField bookIdField = new TextField();

        bookIdField.setPromptText(
                "Example: B001"
        );

        Button returnButton =
                createButton("Return Book");

        Button refreshButton =
                createButton("Refresh");

        form.add(bookLabel, 0, 0);
        form.add(bookIdField, 1, 0);
        form.add(returnButton, 2, 0);
        form.add(refreshButton, 2, 1);

        TableView<IssueRecord> table =
                createTable();

        VBox content = new VBox(10);

        content.getChildren().addAll(
                form,
                new Label("Currently Issued Books"),
                table
        );

        content.setPadding(
                new Insets(0, 30, 25, 30)
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        root.setCenter(content);

        returnButton.setOnAction(event -> {

            String bookId =
                    bookIdField.getText().trim();

            if (bookId.isEmpty()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Missing Information",
                        "Please enter a Book ID."
                );

                return;
            }

            boolean success =
                    issueManager.returnBook(bookId);

            if (success) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Book Returned",
                        "Book " + bookId +
                        " was successfully returned.\n\n" +
                        "Fine calculation was completed " +
                        "according to the 7-day return rule."
                );

                bookIdField.clear();

                refreshTable(table);

            } else {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Return Failed",
                        "Book could not be returned.\n\n" +
                        "Check that the Book ID exists " +
                        "and the book is currently issued."
                );
            }
        });

        refreshButton.setOnAction(event -> {
            refreshTable(table);
        });

        Scene scene =
                new Scene(root, 1050, 650);

        stage.setTitle(
                "SMARTLIB - Return Book"
        );

        stage.setScene(scene);

        stage.show();
    }

    private static TableView<IssueRecord> createTable() {

        TableView<IssueRecord> table =
                new TableView<>();

        TableColumn<IssueRecord, String> bookColumn =
                new TableColumn<>("Book ID");

        bookColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookId")
        );

        TableColumn<IssueRecord, String> studentColumn =
                new TableColumn<>("Student ID");

        studentColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentId")
        );

        TableColumn<IssueRecord, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        TableColumn<IssueRecord, String> dateColumn =
                new TableColumn<>("Issue Date");

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("issueDate")
        );

        table.getColumns().addAll(
                bookColumn,
                studentColumn,
                statusColumn,
                dateColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #D0D5DD;"
        );

        refreshTable(table);

        return table;
    }

    private static void refreshTable(
            TableView<IssueRecord> table
    ) {

        ObservableList<IssueRecord> records =
                FXCollections.observableArrayList(
                        issueManager.getIssueRecords()
                );

        table.setItems(records);
    }

    private static Button createButton(
            String text
    ) {

        Button button = new Button(text);

        button.setPrefHeight(36);

        button.setPadding(
                new Insets(8, 18, 8, 18)
        );

        button.setStyle(
                "-fx-background-color: #1F2937;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 4px;"
        );

        return button;
    }

    private static void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}