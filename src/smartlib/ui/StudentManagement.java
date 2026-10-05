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

import smartlib.model.Student;
import smartlib.service.StudentManager;

public class StudentManagement {

    private static final StudentManager studentManager =
            new StudentManager();

    public static void show(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #F5F6F8;"
        );

        // =========================
        // TOP SECTION
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

        Label title =
                new Label("Student Management");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label subtitle =
                new Label(
                        "Manage student records and information"
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
        // TABLE
        // =========================

        TableView<Student> table =
                new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        TableColumn<Student, String> idColumn =
                new TableColumn<>("Student ID");

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentId")
        );

        TableColumn<Student, String> nameColumn =
                new TableColumn<>("Name");

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        TableColumn<Student, String> departmentColumn =
                new TableColumn<>("Department");

        departmentColumn.setCellValueFactory(
                new PropertyValueFactory<>("department")
        );

        TableColumn<Student, String> yearColumn =
                new TableColumn<>("Year");

        yearColumn.setCellValueFactory(
                new PropertyValueFactory<>("year")
        );

        TableColumn<Student, String> emailColumn =
                new TableColumn<>("Email");

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        table.getColumns().addAll(
                idColumn,
                nameColumn,
                departmentColumn,
                yearColumn,
                emailColumn
        );

        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #D0D5DD;" +
                "-fx-border-radius: 4px;"
        );

        VBox tableBox =
                new VBox(table);

        tableBox.setPadding(
                new Insets(0, 30, 20, 30)
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        root.setCenter(tableBox);

        // =========================
        // BUTTONS
        // =========================

        HBox buttons =
                new HBox(10);

        buttons.setPadding(
                new Insets(0, 30, 25, 30)
        );

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );

        Button addButton =
                createButton("Add Student");

        Button editButton =
                createButton("Edit");

        Button deleteButton =
                createButton("Delete");

        Button refreshButton =
                createButton("Refresh");

        buttons.getChildren().addAll(
                addButton,
                editButton,
                deleteButton,
                refreshButton
        );

        root.setBottom(buttons);

        // =========================
        // LOAD STUDENTS
        // =========================

        loadStudents(table);

        // =========================
        // ADD STUDENT
        // =========================

        addButton.setOnAction(event -> {

            showStudentDialog(
                    stage,
                    table,
                    null
            );
        });

        // =========================
        // EDIT STUDENT
        // =========================

        editButton.setOnAction(event -> {

            Student selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Select Student",
                        "Please select a student to edit."
                );

                return;
            }

            showStudentDialog(
                    stage,
                    table,
                    selected
            );
        });

        // =========================
        // DELETE STUDENT
        // =========================

        deleteButton.setOnAction(event -> {

            Student selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Select Student",
                        "Please select a student to delete."
                );

                return;
            }

            Alert confirm =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

            confirm.setTitle(
                    "Delete Student"
            );

            confirm.setHeaderText(null);

            confirm.setContentText(
                    "Delete student " +
                    selected.getStudentId() +
                    " - " +
                    selected.getName() +
                    "?"
            );

            if (
                    confirm.showAndWait()
                            .orElse(ButtonType.CANCEL)
                            == ButtonType.OK
            ) {

                studentManager.deleteStudent(
                        selected.getStudentId()
                );

                loadStudents(table);
            }
        });

        // =========================
        // REFRESH
        // =========================

        refreshButton.setOnAction(event -> {

            loadStudents(table);
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
                "SMARTLIB - Student Management"
        );

        stage.setScene(scene);

        stage.show();
    }

    // =========================
    // LOAD STUDENTS
    // =========================

    private static void loadStudents(
            TableView<Student> table
    ) {

        ObservableList<Student> students =
                FXCollections.observableArrayList(
                        studentManager.getAllStudents()
                );

        table.setItems(students);
    }

    // =========================
    // STUDENT DIALOG
    // =========================

    private static void showStudentDialog(
            Stage stage,
            TableView<Student> table,
            Student student
    ) {

        boolean editing =
                student != null;

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                editing
                        ? "Edit Student"
                        : "Add Student"
        );

        dialog.setHeaderText(
                editing
                        ? "Update student information"
                        : "Enter student information"
        );

        ButtonType saveButton =
                new ButtonType(
                        editing
                                ? "Update"
                                : "Add",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        saveButton,
                        ButtonType.CANCEL
                );

        GridPane grid =
                new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);

        grid.setPadding(
                new Insets(20)
        );

        TextField idField =
                new TextField();

        TextField nameField =
                new TextField();

        TextField departmentField =
                new TextField();

        TextField yearField =
                new TextField();

        TextField emailField =
                new TextField();

        if (editing) {

            idField.setText(
                    student.getStudentId()
            );

            nameField.setText(
                    student.getName()
            );

            departmentField.setText(
                    student.getDepartment()
            );

            yearField.setText(
                    student.getYear()
            );

            emailField.setText(
                    student.getEmail()
            );

            idField.setDisable(true);
        }

        grid.add(
                new Label("Student ID:"),
                0,
                0
        );

        grid.add(
                idField,
                1,
                0
        );

        grid.add(
                new Label("Name:"),
                0,
                1
        );

        grid.add(
                nameField,
                1,
                1
        );

        grid.add(
                new Label("Department:"),
                0,
                2
        );

        grid.add(
                departmentField,
                1,
                2
        );

        grid.add(
                new Label("Year:"),
                0,
                3
        );

        grid.add(
                yearField,
                1,
                3
        );

        grid.add(
                new Label("Email:"),
                0,
                4
        );

        grid.add(
                emailField,
                1,
                4
        );

        dialog.getDialogPane()
                .setContent(grid);

        Button save =
                (Button) dialog.getDialogPane()
                        .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    if (
                            idField.getText()
                                    .trim()
                                    .isEmpty()
                            ||
                            nameField.getText()
                                    .trim()
                                    .isEmpty()
                            ||
                            departmentField.getText()
                                    .trim()
                                    .isEmpty()
                            ||
                            yearField.getText()
                                    .trim()
                                    .isEmpty()
                            ||
                            emailField.getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Missing Information",
                                "Please fill all fields."
                        );

                        event.consume();

                        return;
                    }

                    if (editing) {

                        studentManager.updateStudent(
                                student.getStudentId(),
                                nameField.getText().trim(),
                                departmentField.getText().trim(),
                                yearField.getText().trim(),
                                emailField.getText().trim()
                        );

                    } else {

                        studentManager.addStudent(
                                idField.getText().trim(),
                                nameField.getText().trim(),
                                departmentField.getText().trim(),
                                yearField.getText().trim(),
                                emailField.getText().trim()
                        );
                    }

                    loadStudents(table);
                }
        );

        dialog.showAndWait();
    }

    // =========================
    // BUTTON STYLE
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