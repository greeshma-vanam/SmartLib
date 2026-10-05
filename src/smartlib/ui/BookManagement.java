package smartlib.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import smartlib.model.Book;
import smartlib.service.BookManager;

public class BookManagement {

    private static final BookManager bookManager =
            new BookManager();

    public static void show(Stage stage) {

        // ===== BACK BUTTON =====

        Button backButton =
                new Button("← Back to Dashboard");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #344054;" +
                "-fx-font-family: Arial;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(event -> {

            String[] userData =
                    (String[]) stage.getUserData();

            if (userData != null && userData.length >= 2) {

                Dashboard.show(
                        stage,
                        userData[0],
                        userData[1]
                );

            } else {

                Dashboard.show(
                        stage,
                        "admin",
                        "ADMIN"
                );
            }
        });


        // ===== TITLE =====

        Label title =
                new Label("Book Management");

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        26
                )
        );

        title.setTextFill(
                Color.web("#172A3A")
        );


        Label subtitle =
                new Label(
                        "View and manage books in the library."
                );

        subtitle.setFont(
                Font.font("Arial", 14)
        );

        subtitle.setTextFill(
                Color.web("#667085")
        );


        // ===== TABLE =====

        TableView<Book> table =
                new TableView<>();

        TableColumn<Book, String> idColumn =
                new TableColumn<>("Book ID");

        idColumn.setPrefWidth(120);

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookId")
        );


        TableColumn<Book, String> titleColumn =
                new TableColumn<>("Title");

        titleColumn.setPrefWidth(250);

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title")
        );


        TableColumn<Book, String> authorColumn =
                new TableColumn<>("Author");

        authorColumn.setPrefWidth(200);

        authorColumn.setCellValueFactory(
                new PropertyValueFactory<>("author")
        );


        TableColumn<Book, String> categoryColumn =
                new TableColumn<>("Category");

        categoryColumn.setPrefWidth(180);

        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category")
        );


        TableColumn<Book, Boolean> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setPrefWidth(130);

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("available")
        );

        statusColumn.setCellFactory(column ->
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

                        } else if (available) {

                            setText("Available");

                        } else {

                            setText("Issued");
                        }
                    }
                }
        );


        table.getColumns().addAll(
                idColumn,
                titleColumn,
                authorColumn,
                categoryColumn,
                statusColumn
        );


        loadBooks(table);

        table.setPlaceholder(
                new Label("No books available.")
        );


        // ===== BUTTONS =====

        Button addButton =
                new Button("Add Book");

        Button editButton =
                new Button("Edit");

        Button deleteButton =
                new Button("Delete");

        Button refreshButton =
                new Button("Refresh");


        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(refreshButton);


        // ===== ADD BOOK =====

        addButton.setOnAction(event -> {

            showAddBookDialog(
                    stage,
                    table
            );

        });


        // ===== REFRESH =====

        refreshButton.setOnAction(event -> {

            loadBooks(table);

        });


        // ===== EDIT =====

        editButton.setOnAction(event -> {

            Book selectedBook =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selectedBook == null) {

                showAlert(
                        "No Book Selected",
                        "Please select a book to edit."
                );

                return;
            }

            showEditBookDialog(
                    stage,
                    table,
                    selectedBook
            );
        });


        // ===== DELETE =====

        deleteButton.setOnAction(event -> {

            Book selectedBook =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selectedBook == null) {

                showAlert(
                        "No Book Selected",
                        "Please select a book to delete."
                );

                return;
            }


            Alert confirmation =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

            confirmation.setTitle(
                    "Delete Book"
            );

            confirmation.setHeaderText(
                    "Delete selected book?"
            );

            confirmation.setContentText(
                    "Book: " +
                    selectedBook.getTitle()
            );


            confirmation.showAndWait()
                    .ifPresent(response -> {

                        if (response ==
                                ButtonType.OK) {

                            boolean deleted =
                                    bookManager.deleteBook(
                                            selectedBook.getBookId()
                                    );

                            if (deleted) {

                                loadBooks(table);

                                showAlert(
                                        "Success",
                                        "Book deleted successfully."
                                );

                            } else {

                                showAlert(
                                        "Error",
                                        "Unable to delete the book."
                                );
                            }
                        }
                    });
        });


        HBox buttons =
                new HBox(
                        10,
                        addButton,
                        editButton,
                        deleteButton,
                        refreshButton
                );

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );


        // ===== CONTENT =====

        VBox content =
                new VBox(
                        12,
                        backButton,
                        title,
                        subtitle,
                        table,
                        buttons
                );

        content.setPadding(
                new Insets(30)
        );

        VBox.setVgrow(
                table,
                javafx.scene.layout.Priority.ALWAYS
        );


        // ===== ROOT =====

        BorderPane root =
                new BorderPane();

        root.setCenter(content);

        root.setStyle(
                "-fx-background-color: #F7F8FA;"
        );


        Scene scene =
                new Scene(
                        root,
                        1000,
                        650
                );


        stage.setTitle(
                "SMARTLIB - Book Management"
        );

        stage.setScene(scene);

        stage.show();
    }


    // =====================================================
    // LOAD BOOKS
    // =====================================================

    private static void loadBooks(
            TableView<Book> table
    ) {

        ObservableList<Book> books =
                FXCollections.observableArrayList(
                        bookManager.getAllBooks()
                );

        table.setItems(books);
    }


    // =====================================================
    // ADD BOOK DIALOG
    // =====================================================

    private static void showAddBookDialog(
            Stage owner,
            TableView<Book> table
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Add Book"
        );

        dialog.setHeaderText(
                "Add a new book to the library"
        );


        TextField idField =
                new TextField();

        idField.setPromptText(
                "Example: B003"
        );


        TextField titleField =
                new TextField();

        titleField.setPromptText(
                "Book title"
        );


        TextField authorField =
                new TextField();

        authorField.setPromptText(
                "Author name"
        );


        TextField categoryField =
                new TextField();

        categoryField.setPromptText(
                "Category"
        );


        GridPane grid =
                new GridPane();

        grid.setHgap(10);
        grid.setVgap(12);

        grid.setPadding(
                new Insets(20)
        );


        grid.add(
                new Label("Book ID:"),
                0,
                0
        );

        grid.add(
                idField,
                1,
                0
        );


        grid.add(
                new Label("Title:"),
                0,
                1
        );

        grid.add(
                titleField,
                1,
                1
        );


        grid.add(
                new Label("Author:"),
                0,
                2
        );

        grid.add(
                authorField,
                1,
                2
        );


        grid.add(
                new Label("Category:"),
                0,
                3
        );

        grid.add(
                categoryField,
                1,
                3
        );


        dialog.getDialogPane()
                .setContent(grid);


        ButtonType addButton =
                new ButtonType(
                        "Add",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        addButton,
                        ButtonType.CANCEL
                );


        dialog.setResultConverter(
                button -> {

                    if (button == addButton) {

                        String id =
                                idField.getText().trim();

                        String title =
                                titleField.getText().trim();

                        String author =
                                authorField.getText().trim();

                        String category =
                                categoryField.getText().trim();


                        if (
                                id.isEmpty()
                                ||
                                title.isEmpty()
                                ||
                                author.isEmpty()
                                ||
                                category.isEmpty()
                        ) {

                            showAlert(
                                    "Missing Information",
                                    "Please fill in all fields."
                            );

                            return null;
                        }


                        boolean added =
                                bookManager.addBook(
                                        id,
                                        title,
                                        author,
                                        category
                                );


                        if (added) {

                            loadBooks(table);

                            showAlert(
                                    "Success",
                                    "Book added successfully."
                            );

                        } else {

                            showAlert(
                                    "Error",
                                    "Could not add the book.\n" +
                                    "The Book ID may already exist."
                            );
                        }
                    }

                    return null;
                }
        );


        dialog.showAndWait();
    }


    // =====================================================
    // EDIT BOOK DIALOG
    // =====================================================

    private static void showEditBookDialog(
            Stage owner,
            TableView<Book> table,
            Book book
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Edit Book"
        );

        dialog.setHeaderText(
                "Edit book information"
        );


        TextField titleField =
                new TextField(
                        book.getTitle()
                );

        TextField authorField =
                new TextField(
                        book.getAuthor()
                );

        TextField categoryField =
                new TextField(
                        book.getCategory()
                );


        GridPane grid =
                new GridPane();

        grid.setHgap(10);
        grid.setVgap(12);

        grid.setPadding(
                new Insets(20)
        );


        grid.add(
                new Label("Book ID:"),
                0,
                0
        );

        Label idLabel =
                new Label(
                        book.getBookId()
                );

        grid.add(
                idLabel,
                1,
                0
        );


        grid.add(
                new Label("Title:"),
                0,
                1
        );

        grid.add(
                titleField,
                1,
                1
        );


        grid.add(
                new Label("Author:"),
                0,
                2
        );

        grid.add(
                authorField,
                1,
                2
        );


        grid.add(
                new Label("Category:"),
                0,
                3
        );

        grid.add(
                categoryField,
                1,
                3
        );


        dialog.getDialogPane()
                .setContent(grid);


        ButtonType saveButton =
                new ButtonType(
                        "Save",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        saveButton,
                        ButtonType.CANCEL
                );


        dialog.setResultConverter(
                button -> {

                    if (button == saveButton) {

                        boolean updated =
                                bookManager.updateBook(
                                        book.getBookId(),
                                        titleField.getText().trim(),
                                        authorField.getText().trim(),
                                        categoryField.getText().trim()
                                );


                        if (updated) {

                            loadBooks(table);

                            showAlert(
                                    "Success",
                                    "Book updated successfully."
                            );

                        } else {

                            showAlert(
                                    "Error",
                                    "Unable to update the book."
                            );
                        }
                    }

                    return null;
                }
        );


        dialog.showAndWait();
    }


    // =====================================================
    // ALERT
    // =====================================================

    private static void showAlert(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    // =====================================================
    // BUTTON STYLE
    // =====================================================

    private static void styleButton(
            Button button
    ) {

        button.setPrefHeight(38);

        button.setPadding(
                new Insets(
                        0,
                        16,
                        0,
                        16
                )
        );

        button.setStyle(
                "-fx-background-color: #1F4E79;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: Arial;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 3px;" +
                "-fx-cursor: hand;"
        );
    }
}