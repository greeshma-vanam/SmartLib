package smartlib.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import smartlib.database.DatabaseInitializer;
import smartlib.model.User;
import smartlib.service.LoginManager;

public class SmartLibApp extends Application {

    private LoginManager loginManager;

    @Override
    public void start(Stage stage) {

        // Initialize database BEFORE login manager
        DatabaseInitializer.initializeDatabase();

        loginManager = new LoginManager();

        // ===== LEFT SIDE =====

        Label logo = new Label("SMARTLIB");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        logo.setTextFill(Color.WHITE);

        Label subtitle = new Label(
                "Smart Library Management System"
        );
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#D5DCE5"));

        Label description = new Label(
                "Manage books, students,\n" +
                "issues, returns and reports\n" +
                "from one application."
        );

        description.setFont(Font.font("Arial", 15));
        description.setTextFill(Color.web("#D5DCE5"));

        VBox leftBox = new VBox(
                18,
                logo,
                subtitle,
                description
        );

        leftBox.setAlignment(Pos.TOP_LEFT);
        leftBox.setPadding(
                new Insets(55, 45, 40, 45)
        );
        leftBox.setPrefWidth(390);

        leftBox.setStyle(
                "-fx-background-color: #172A3A;"
        );

        // ===== RIGHT SIDE =====

        Label welcome = new Label("Welcome back");

        welcome.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        26
                )
        );

        welcome.setTextFill(
                Color.web("#172A3A")
        );

        Label loginText = new Label(
                "Sign in to continue to SMARTLIB"
        );

        loginText.setFont(
                Font.font("Arial", 14)
        );

        loginText.setTextFill(
                Color.web("#667085")
        );

        Label usernameLabel =
                new Label("Username");

        usernameLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        TextField usernameField =
                new TextField();

        usernameField.setPromptText(
                "Enter username"
        );

        usernameField.setPrefHeight(42);
        usernameField.setMaxWidth(330);

        Label passwordLabel =
                new Label("Password");

        passwordLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Enter password"
        );

        passwordField.setPrefHeight(42);
        passwordField.setMaxWidth(330);

        Button loginButton =
                new Button("Login");

        loginButton.setPrefWidth(330);
        loginButton.setPrefHeight(42);

        loginButton.setStyle(
                "-fx-background-color: #1F4E79;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 3px;" +
                "-fx-cursor: hand;"
        );

        Label message =
                new Label("");

        message.setFont(
                Font.font("Arial", 12)
        );

        message.setTextFill(
                Color.web("#B42318")
        );

        // ===== LOGIN ACTION =====

        loginButton.setOnAction(event -> {

            String username =
                    usernameField.getText().trim();

            String password =
                    passwordField.getText().trim();

            if (
                    username.isEmpty()
                    ||
                    password.isEmpty()
            ) {

                message.setText(
                        "Please enter username and password."
                );

                return;
            }

            User user =
                    loginManager.login(
                            username,
                            password
                    );

            if (user != null) {

                System.out.println(
                        "[Login] User: " +
                        user.getUsername() +
                        " | Role: " +
                        user.getRole()
                );

                Dashboard.show(
                        stage,
                        user.getUsername(),
                        user.getRole()
                );

            } else {

                message.setTextFill(
                        Color.web("#B42318")
                );

                message.setText(
                        "Invalid username or password."
                );
            }
        });

        // ===== LOGIN FORM =====

        VBox form = new VBox(
                8,
                welcome,
                loginText,
                new Label(""),
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                new Label(""),
                loginButton,
                message
        );

        form.setAlignment(
                Pos.TOP_LEFT
        );

        form.setMaxWidth(330);

        VBox rightBox =
                new VBox(form);

        rightBox.setAlignment(
                Pos.CENTER
        );

        rightBox.setPadding(
                new Insets(40)
        );

        rightBox.setStyle(
                "-fx-background-color: #F7F8FA;"
        );

        // ===== MAIN LAYOUT =====

        BorderPane root =
                new BorderPane();

        root.setLeft(leftBox);
        root.setCenter(rightBox);

        Scene scene =
                new Scene(
                        root,
                        950,
                        600
                );

        stage.setTitle(
                "SMARTLIB - Login"
        );

        stage.setScene(scene);

        stage.setMinWidth(850);
        stage.setMinHeight(550);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}