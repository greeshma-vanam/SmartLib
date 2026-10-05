package smartlib.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import smartlib.model.Book;
import smartlib.model.Student;
import smartlib.service.BookManager;
import smartlib.service.StudentManager;

public class Search {

    private static final BookManager bookManager =
            new BookManager();

    private static final StudentManager studentManager =
            new StudentManager();

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
                new Label("Search");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label subtitle =
                new Label(
                        "Search books and students"
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
        // SEARCH AREA
        // =========================

        VBox content =
                new VBox(15);

        content.setPadding(
                new Insets(10, 30, 30, 30)
        );

        HBox searchBar =
                new HBox(10);

        ComboBox<String> typeBox =
                new ComboBox<>();

        typeBox.getItems().addAll(
                "Books",
                "Students"
        );

        typeBox.setValue("Books");

        typeBox.setPrefWidth(130);

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Enter title, author, ID, name..."
        );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        Button searchButton =
                createButton("Search");

        Button clearButton =
                createButton("Clear");

        searchBar.getChildren().addAll(
                typeBox,
                searchField,
                searchButton,
                clearButton
        );

        // =========================
        // RESULT AREA
        // =========================

        TableView<Book> bookTable =
                createBookTable();

        TableView<Student> studentTable =
                createStudentTable();

        content.getChildren().addAll(
                searchBar,
                bookTable
        );

        VBox.setVgrow(
                bookTable,
                Priority.ALWAYS
        );

        root.setCenter(content);

        // =========================
        // SEARCH
        // =========================

        searchButton.setOnAction(event -> {

            String query =
                    searchField.getText()
                            .trim()
                            .toLowerCase();

            if (query.isEmpty()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Search",
                        "Please enter something to search."
                );

                return;
            }

            if (typeBox.getValue()
                    .equals("Books")) {

                searchBooks(
                        query,
                        bookTable
                );

                content.getChildren().set(
                        1,
                        bookTable
                );

                VBox.setVgrow(
                        bookTable,
                        Priority.ALWAYS
                );

            } else {

                searchStudents(
                        query,
                        studentTable
                );

                content.getChildren().set(
                        1,
                        studentTable
                );

                VBox.setVgrow(
                        studentTable,
                        Priority.ALWAYS
                );
            }
        });

        // =========================
        // CLEAR
        // =========================

        clearButton.setOnAction(event -> {

            searchField.clear();

            typeBox.setValue("Books");

            content.getChildren().set(
                    1,
                    bookTable
            );

            bookTable.getItems().clear();
            studentTable.getItems().clear();
        });

        // =========================
        // TYPE CHANGE
        // =========================

        typeBox.setOnAction(event -> {

            searchField.clear();

            if (typeBox.getValue()
                    .equals("Books")) {

                content.getChildren().set(
                        1,
                        bookTable
                );

            } else {

                content.getChildren().set(
                        1,
                        studentTable
                );
            }
        });

        Scene scene =
                new Scene(
                        root,
                        1050,
                        650
                );

        stage.setTitle(
                "SMARTLIB - Search"
        );

        stage.setScene(scene);

        stage.show();
    }

    // =========================
    // BOOK TABLE
    // =========================

    private static TableView<Book> createBookTable() {

        TableView<Book> table =
                new TableView<>();

        TableColumn<Book, String> id =
                new TableColumn<>("Book ID");

        id.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "bookId"
                )
        );

        TableColumn<Book, String> title =
                new TableColumn<>("Title");

        title.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "title"
                )
        );

        TableColumn<Book, String> author =
                new TableColumn<>("Author");

        author.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "author"
                )
        );

        TableColumn<Book, String> category =
                new TableColumn<>("Category");

        category.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "category"
                )
        );

        TableColumn<Book, Boolean> status =
                new TableColumn<>("Status");

        status.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "available"
                )
        );

        status.setCellFactory(column ->
                new TableCell<Book, Boolean>() {

                    @Override
                    protected void updateItem(
                            Boolean available,
                            boolean empty
                    ) {

                        super.updateItem(
                                available,
                                empty
                        );

                        if (empty || available == null) {

                            setText(null);

                        } else {

                            setText(
                                    available
                                            ? "Available"
                                            : "Issued"
                            );
                        }
                    }
                }
        );

        table.getColumns().addAll(
                id,
                title,
                author,
                category,
                status
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        return table;
    }

    // =========================
    // STUDENT TABLE
    // =========================

    private static TableView<Student> createStudentTable() {

        TableView<Student> table =
                new TableView<>();

        TableColumn<Student, String> id =
                new TableColumn<>("Student ID");

        id.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "studentId"
                )
        );

        TableColumn<Student, String> name =
                new TableColumn<>("Name");

        name.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "name"
                )
        );

        TableColumn<Student, String> department =
                new TableColumn<>("Department");

        department.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "department"
                )
        );

        TableColumn<Student, String> year =
                new TableColumn<>("Year");

        year.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "year"
                )
        );

        TableColumn<Student, String> email =
                new TableColumn<>("Email");

        email.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "email"
                )
        );

        table.getColumns().addAll(
                id,
                name,
                department,
                year,
                email
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        return table;
    }

    // =========================
    // SEARCH BOOKS
    // =========================

    private static void searchBooks(
            String query,
            TableView<Book> table
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        bookManager.getAllBooks()
                                .stream()
                                .filter(book ->
                                        book.getBookId()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        book.getTitle()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        book.getAuthor()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        book.getCategory()
                                                .toLowerCase()
                                                .contains(query)
                                )
                                .toList()
                )
        );
    }

    // =========================
    // SEARCH STUDENTS
    // =========================

    private static void searchStudents(
            String query,
            TableView<Student> table
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        studentManager.getAllStudents()
                                .stream()
                                .filter(student ->
                                        student.getStudentId()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        student.getName()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        student.getDepartment()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        student.getYear()
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        student.getEmail()
                                                .toLowerCase()
                                                .contains(query)
                                )
                                .toList()
                )
        );
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