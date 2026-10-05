package smartlib.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import smartlib.model.IssueRecord;
import smartlib.service.BookManager;
import smartlib.service.IssueManager;
import smartlib.service.StudentManager;

public class IssueBook {

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
                new Insets(25, 30, 20, 30)
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

        Label title =
                new Label("Issue Book");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label subtitle =
                new Label(
                        "Issue an available book to a student"
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
        // FORM
        // =========================

        GridPane form =
                new GridPane();

        form.setHgap(15);
        form.setVgap(12);

        form.setPadding(
                new Insets(10, 30, 20, 30)
        );

        Label bookLabel =
                new Label("Book ID:");

        TextField bookIdField =
                new TextField();

        bookIdField.setPromptText(
                "Example: B001"
        );

        Label studentLabel =
                new Label("Student ID:");

        TextField studentIdField =
                new TextField();

        studentIdField.setPromptText(
                "Example: S001"
        );

        Button issueButton =
                createButton("Issue Book");

        Button refreshButton =
                createButton("Refresh");

        form.add(
                bookLabel,
                0,
                0
        );

        form.add(
                bookIdField,
                1,
                0
        );

        form.add(
                studentLabel,
                0,
                1
        );

        form.add(
                studentIdField,
                1,
                1
        );

        form.add(
                issueButton,
                2,
                0
        );

        form.add(
                refreshButton,
                2,
                1
        );

        root.setCenter(
                createCenterContent(
                        form,
                        bookIdField,
                        studentIdField,
                        issueButton,
                        refreshButton
                )
        );

        // =========================
        // ISSUE ACTION
        // =========================

        issueButton.setOnAction(event -> {

            String bookId =
                    bookIdField.getText().trim();

            String studentId =
                    studentIdField.getText().trim();

            if (
                    bookId.isEmpty()
                    ||
                    studentId.isEmpty()
            ) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Missing Information",
                        "Please enter both Book ID and Student ID."
                );

                return;
            }

            boolean success =
                    issueManager.issueBook(
                            bookId,
                            studentId
                    );

            if (success) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Book Issued",
                        "Book " +
                        bookId +
                        " was successfully issued to student " +
                        studentId +
                        ".\n\nReturn period: 7 days."
                );

                bookIdField.clear();
                studentIdField.clear();

                refreshTable(
                        getTableFromCenter(root)
                );

            } else {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Issue Failed",
                        "Book could not be issued.\n\n" +
                        "Check that:\n" +
                        "• Book ID exists\n" +
                        "• Student ID exists\n" +
                        "• Book is available"
                );
            }
        });

        refreshButton.setOnAction(event -> {

            refreshTable(
                    getTableFromCenter(root)
            );
        });

        // =========================
        // SCENE
        // =========================

        Scene scene =
                new Scene(
                        root,
                        1050,
                        650
                );

        stage.setTitle(
                "SMARTLIB - Issue Book"
        );

        stage.setScene(scene);

        stage.show();
    }

    // =========================
    // CENTER CONTENT
    // =========================

    private static VBox createCenterContent(
            GridPane form,
            TextField bookIdField,
            TextField studentIdField,
            Button issueButton,
            Button refreshButton
    ) {

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

        VBox content =
                new VBox(10);

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

        return content;
    }

    // =========================
    // REFRESH TABLE
    // =========================

    private static void refreshTable(
            TableView<IssueRecord> table
    ) {

        if (table == null) {
            return;
        }

        ObservableList<IssueRecord> records =
                FXCollections.observableArrayList(
                        issueManager.getIssueRecords()
                );

        table.setItems(records);
    }

    // =========================
    // FIND TABLE
    // =========================

    @SuppressWarnings("unchecked")
    private static TableView<IssueRecord>
    getTableFromCenter(BorderPane root) {

        VBox content =
                (VBox) root.getCenter();

        for (javafx.scene.Node node :
                content.getChildren()) {

            if (node instanceof TableView<?>) {

                return (TableView<IssueRecord>) node;
            }
        }

        return null;
    }

    // =========================
    // BUTTON
    // =========================

    private static Button createButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(36);

        button.setPadding(
                new Insets(
                        8,
                        18,
                        8,
                        18
                )
        );

        button.setStyle(
                "-fx-background-color: #1F2937;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 4px;"
        );

        return button;
    }

    // =========================
    // ALERT
    // =========================

    private static void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}