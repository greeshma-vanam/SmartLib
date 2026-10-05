package smartlib.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import smartlib.service.BookManager;
import smartlib.service.NotificationManager;
import smartlib.service.StudentManager;

import java.util.List;

public class Dashboard {

    private static final BookManager bookManager =
            new BookManager();

    private static final StudentManager studentManager =
            new StudentManager();

    public static void show(
            Stage stage,
            String username,
            String role
    ) {

        boolean isStudent =
                "STUDENT".equalsIgnoreCase(role);

        stage.setUserData(
                new String[]{username, role}
        );

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #F6F8FB;"
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar = new VBox(7);

        sidebar.setPrefWidth(245);

        sidebar.setPadding(
                new Insets(24, 14, 18, 14)
        );

        sidebar.setStyle(
                "-fx-background-color: #0F172A;"
        );

        // =====================================================
        // SMARTLIB LOGO
        // =====================================================

        HBox logoBox =
                createSmartLibLogo();

        Label tagline =
                new Label(
                        "SMART LIBRARY SYSTEM"
                );

        tagline.setStyle(
                "-fx-text-fill: #94A3B8;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );

        tagline.setPadding(
                new Insets(0, 0, 12, 43)
        );

        // =====================================================
        // USER LABEL
        // =====================================================

        Label userLabel =
                new Label(
                        username + "  •  " + role
                );

        userLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        userLabel.setPadding(
                new Insets(10, 12, 10, 12)
        );

        userLabel.setStyle(
                "-fx-background-color: #172554;" +
                "-fx-background-radius: 5px;" +
                "-fx-text-fill: #E2E8F0;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Region topSpace =
                new Region();

        topSpace.setPrefHeight(10);

        // =====================================================
        // OVERVIEW
        // =====================================================

        Label overviewLabel =
                createSectionLabel("OVERVIEW");

        Button dashboardButton =
                createMenuButton(
                        "Dashboard",
                        "#0F766E"
                );

        Button searchButton =
                createMenuButton(
                        "Search",
                        "#1E293B"
                );

        dashboardButton.setOnAction(
                event ->
                        Dashboard.show(
                                stage,
                                username,
                                role
                        )
        );

        searchButton.setOnAction(
                event ->
                        Search.show(stage)
        );

        sidebar.getChildren().addAll(
                logoBox,
                tagline,
                userLabel,
                topSpace,
                overviewLabel,
                dashboardButton,
                searchButton
        );

        // =====================================================
        // MANAGEMENT
        // =====================================================

        if (!isStudent) {

            Label managementLabel =
                    createSectionLabel(
                            "MANAGEMENT"
                    );

            Button booksButton =
                    createMenuButton(
                            "Book Management",
                            "#1E293B"
                    );

            Button studentsButton =
                    createMenuButton(
                            "Student Management",
                            "#1E293B"
                    );

            Button issueButton =
                    createMenuButton(
                            "Issue Book",
                            "#1E293B"
                    );

            Button returnButton =
                    createMenuButton(
                            "Return Book",
                            "#1E293B"
                    );

            Button reportsButton =
                    createMenuButton(
                            "Reports",
                            "#1E293B"
                    );

            booksButton.setOnAction(
                    event ->
                            BookManagement.show(stage)
            );

            studentsButton.setOnAction(
                    event ->
                            StudentManagement.show(stage)
            );

            issueButton.setOnAction(
                    event ->
                            IssueBook.show(stage)
            );

            returnButton.setOnAction(
                    event ->
                            ReturnBook.show(stage)
            );

            reportsButton.setOnAction(
                    event ->
                            Reports.show(stage)
            );

            sidebar.getChildren().addAll(
                    managementLabel,
                    booksButton,
                    studentsButton,
                    issueButton,
                    returnButton,
                    reportsButton
            );
        }

        // =====================================================
        // SIDEBAR BOTTOM
        // =====================================================

        Region sidebarSpacer =
                new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        Label loggedIn =
                new Label("SIGNED IN AS");

        loggedIn.setStyle(
                "-fx-text-fill: #64748B;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;"
        );

        Label bottomUser =
                new Label(username);

        bottomUser.setStyle(
                "-fx-text-fill: #E2E8F0;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        Button logoutButton =
                createMenuButton(
                        "Logout",
                        "#7F1D1D"
                );

        logoutButton.setOnAction(
                event ->
                        new SmartLibApp().start(stage)
        );

        sidebar.getChildren().addAll(
                sidebarSpacer,
                loggedIn,
                bottomUser,
                logoutButton
        );

        root.setLeft(sidebar);

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(
                        34,
                        38,
                        34,
                        38
                )
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(5);

        Label heading =
                new Label("Dashboard");

        heading.setStyle(
                "-fx-font-size: 29px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label welcome =
                new Label(
                        "Welcome back, " +
                        username +
                        ". Here's your library at a glance."
                );

        welcome.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #667085;"
        );

        titleBox.getChildren().addAll(
                heading,
                welcome
        );

        Region headerSpacer =
                new Region();

        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );

        // =====================================================
        // NOTIFICATION BELL
        // =====================================================

        Button notificationButton =
                createNotificationButton(
                        stage,
                        username,
                        role
                );

        // =====================================================
        // ROLE BADGE
        // =====================================================

        Label roleBadge =
                new Label(
                        role.toUpperCase()
                );

        roleBadge.setPadding(
                new Insets(9, 15, 9, 15)
        );

        roleBadge.setStyle(
                "-fx-background-color: #E0F2FE;" +
                "-fx-text-fill: #0369A1;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;"
        );

        header.getChildren().addAll(
                titleBox,
                headerSpacer,
                notificationButton,
                roleBadge
        );

        // =====================================================
        // STUDENT RETURN REMINDERS
        // =====================================================

        if (isStudent) {

            NotificationManager.generateReturnReminders(
                    username
            );
        }

        // =====================================================
        // STATISTICS
        // =====================================================

        HBox stats =
                new HBox(16);

        Label totalBooksValue =
                createValueLabel(
                        bookManager.getTotalBooksCount()
                );

        Label availableBooksValue =
                createValueLabel(
                        bookManager.getAvailableBooksCount()
                );

        Label issuedBooksValue =
                createValueLabel(
                        bookManager.getIssuedBooksCount()
                );

        Label studentsValue =
                createValueLabel(
                        studentManager.getTotalStudentsCount()
                );

        stats.getChildren().addAll(

                createStatCard(
                        "TOTAL BOOKS",
                        totalBooksValue,
                        "#2563EB",
                        "LIB"
                ),

                createStatCard(
                        "AVAILABLE",
                        availableBooksValue,
                        "#16A34A",
                        "AVL"
                ),

                createStatCard(
                        "ISSUED",
                        issuedBooksValue,
                        "#EA580C",
                        "ISS"
                ),

                createStatCard(
                        "STUDENTS",
                        studentsValue,
                        "#0F766E",
                        "STD"
                )
        );

        // =====================================================
        // CONTENT
        // =====================================================

        content.getChildren().addAll(
                header,
                stats
        );

        StackPane centerWrapper =
                new StackPane(content);

        centerWrapper.setAlignment(
                Pos.TOP_LEFT
        );

        root.setCenter(
                centerWrapper
        );

        // =====================================================
        // FLOATING AI BUTTON
        // =====================================================

        Button aiButton =
                createAIButton();

        StackPane mainLayer =
                new StackPane();

        mainLayer.getChildren().addAll(
                root,
                aiButton
        );

        StackPane.setAlignment(
                aiButton,
                Pos.BOTTOM_RIGHT
        );

        StackPane.setMargin(
                aiButton,
                new Insets(
                        0,
                        28,
                        25,
                        0
                )
        );

        Scene scene =
                new Scene(
                        mainLayer,
                        1180,
                        720
                );

        stage.setTitle(
                "SMARTLIB - Dashboard"
        );

        stage.setScene(scene);

        stage.show();
    }

    // =========================================================
    // NOTIFICATION BUTTON
    // =========================================================

    private static Button createNotificationButton(
            Stage stage,
            String username,
            String role
    ) {

        int unreadCount =
                NotificationManager.getUnreadCount(
                        username
                );

        StackPane wrapper =
                new StackPane();

        Button bell =
                new Button("●");

        bell.setPrefSize(
                42,
                42
        );

        bell.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #172033;" +
                "-fx-font-size: 14px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;" +
                "-fx-cursor: hand;"
        );

        Tooltip.install(
                bell,
                new Tooltip("Notifications")
        );

        Label countLabel =
                new Label(
                        unreadCount > 99
                                ? "99+"
                                : String.valueOf(
                                        unreadCount
                                )
                );

        countLabel.setStyle(
                "-fx-background-color: #DC2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 3px 5px 3px 5px;" +
                "-fx-background-radius: 10px;"
        );

        if (unreadCount == 0) {
            countLabel.setVisible(false);
        }

        StackPane.setAlignment(
                countLabel,
                Pos.TOP_RIGHT
        );

        StackPane.setMargin(
                countLabel,
                new Insets(1, 1, 0, 0)
        );

        wrapper.getChildren().addAll(
                bell,
                countLabel
        );

        bell.setOnAction(
                event ->
                        showNotifications(
                                stage,
                                username,
                                role
                        )
        );

        return createWrapperButton(wrapper);
    }

    private static Button createWrapperButton(
            StackPane wrapper
    ) {

        Button button =
                new Button();

        button.setGraphic(wrapper);

        button.setPrefSize(
                42,
                42
        );

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;"
        );

        return button;
    }

    // =========================================================
    // NOTIFICATION WINDOW
    // =========================================================

    private static void showNotifications(
            Stage owner,
            String username,
            String role
    ) {

        Stage notificationStage =
                new Stage();

        notificationStage.setTitle(
                "SMARTLIB - Notifications"
        );

        notificationStage.initOwner(owner);

        VBox root =
                new VBox(12);

        root.setPadding(
                new Insets(22)
        );

        root.setPrefSize(
                520,
                580
        );

        root.setStyle(
                "-fx-background-color: #F6F8FB;"
        );

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label("Notifications");

        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button readAll =
                new Button("Mark all as read");

        readAll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #0F766E;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                title,
                spacer,
                readAll
        );

        VBox notificationList =
                new VBox(10);

        ScrollPane scrollPane =
                new ScrollPane(
                        notificationList
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        Runnable refresh =
                () -> {

                    notificationList
                            .getChildren()
                            .clear();

                    List<NotificationManager.Notification>
                            notifications =
                            NotificationManager
                                    .getNotifications(
                                            username,
                                            role
                                    );

                    if (notifications.isEmpty()) {

                        Label empty =
                                new Label(
                                        "No notifications."
                                );

                        empty.setStyle(
                                "-fx-text-fill: #667085;" +
                                "-fx-font-size: 13px;"
                        );

                        empty.setPadding(
                                new Insets(25)
                        );

                        notificationList
                                .getChildren()
                                .add(empty);

                        return;
                    }

                    for (
                            NotificationManager.Notification notification
                            : notifications
                    ) {

                        VBox card =
                                createNotificationCard(
                                        notification,
                                        username,
                                        notificationList
                                );

                        notificationList
                                .getChildren()
                                .add(card);
                    }
                };

        readAll.setOnAction(
                event -> {

                    NotificationManager
                            .markAllAsRead(
                                    username
                            );

                    refresh.run();
                }
        );

        refresh.run();

        root.getChildren().addAll(
                header,
                scrollPane
        );

        Scene scene =
                new Scene(
                        root,
                        520,
                        580
                );

        notificationStage.setScene(
                scene
        );

        notificationStage.show();
    }

    // =========================================================
    // NOTIFICATION CARD
    // =========================================================

    private static VBox createNotificationCard(
            NotificationManager.Notification notification,
            String username,
            VBox parent
    ) {

        VBox card =
                new VBox(6);

        card.setPadding(
                new Insets(15)
        );

        String background =
                notification.isRead()
                        ? "white"
                        : "#EFF6FF";

        card.setStyle(
                "-fx-background-color: " +
                background +
                ";" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;"
        );

        Label title =
                new Label(
                        notification.getTitle()
                );

        title.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        Label message =
                new Label(
                        notification.getMessage()
                );

        message.setWrapText(true);

        message.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #475467;"
        );

        Label date =
                new Label(
                        notification.getCreatedAt()
                );

        date.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: #98A2B3;"
        );

        card.getChildren().addAll(
                title,
                message,
                date
        );

        if (!notification.isRead()) {

            card.setOnMouseClicked(
                    event -> {

                        NotificationManager
                                .markAsRead(
                                        notification.getId()
                                );

                        card.setStyle(
                                "-fx-background-color: white;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-width: 1px;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-border-radius: 6px;"
                        );
                    }
            );
        }

        return card;
    }

    // =========================================================
    // SMARTLIB LOGO
    // =========================================================

    private static HBox createSmartLibLogo() {

        HBox logo =
                new HBox(10);

        logo.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane icon =
                new StackPane();

        icon.setPrefSize(
                36,
                36
        );

        Rectangle book =
                new Rectangle(
                        26,
                        21
                );

        book.setArcWidth(4);
        book.setArcHeight(4);

        book.setFill(
                Color.web("#0F766E")
        );

        Line centerLine =
                new Line(
                        18,
                        8,
                        18,
                        28
                );

        centerLine.setStroke(
                Color.WHITE
        );

        centerLine.setStrokeWidth(2);

        Circle dot =
                new Circle(
                        3,
                        Color.web("#38BDF8")
                );

        StackPane.setAlignment(
                dot,
                Pos.TOP_RIGHT
        );

        StackPane.setMargin(
                dot,
                new Insets(
                        1,
                        1,
                        0,
                        0
                )
        );

        icon.getChildren().addAll(
                book,
                centerLine,
                dot
        );

        Label name =
                new Label("SMARTLIB");

        name.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );

        logo.getChildren().addAll(
                icon,
                name
        );

        return logo;
    }

    // =========================================================
    // SIDEBAR BUTTON
    // =========================================================

    private static Button createMenuButton(
            String text,
            String backgroundColor
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        11,
                        13,
                        11,
                        15
                )
        );

        button.setStyle(
                "-fx-background-color: " +
                backgroundColor +
                ";" +
                "-fx-text-fill: #E2E8F0;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " +
                (
                        backgroundColor.equals(
                                "#0F766E"
                        )
                                ? "bold;"
                                : "normal;"
                ) +
                "-fx-background-radius: 5px;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // SECTION LABEL
    // =========================================================

    private static Label createSectionLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setPadding(
                new Insets(
                        13,
                        0,
                        5,
                        7
                )
        );

        label.setStyle(
                "-fx-text-fill: #64748B;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1.2px;"
        );

        return label;
    }

    // =========================================================
    // VALUE LABEL
    // =========================================================

    private static Label createValueLabel(
            int value
    ) {

        Label label =
                new Label(
                        String.valueOf(value)
                );

        label.setStyle(
                "-fx-font-size: 29px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172033;"
        );

        return label;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private static VBox createStatCard(
            String title,
            Label value,
            String accent,
            String code
    ) {

        VBox card =
                new VBox(8);

        card.setPrefHeight(135);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-background-radius: 7px;" +
                "-fx-border-radius: 7px;"
        );

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill: #667085;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 0.8px;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label codeLabel =
                new Label(code);

        codeLabel.setPadding(
                new Insets(
                        4,
                        7,
                        4,
                        7
                )
        );

        codeLabel.setStyle(
                "-fx-background-color: " +
                accent +
                ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 3px;"
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                codeLabel
        );

        Region line =
                new Region();

        line.setPrefHeight(3);

        line.setMaxWidth(42);

        line.setStyle(
                "-fx-background-color: " +
                accent +
                ";" +
                "-fx-background-radius: 2px;"
        );

        card.getChildren().addAll(
                top,
                line,
                value
        );

        return card;
    }

    // =========================================================
    // FLOATING AI BUTTON
    // =========================================================

    private static Button createAIButton() {

        Button button =
                new Button("AI");

        button.setPrefSize(
                58,
                58
        );

        button.setMinSize(
                58,
                58
        );

        button.setMaxSize(
                58,
                58
        );

        button.setTooltip(
                new Tooltip(
                        "AI Assistant"
                )
        );

        button.setStyle(
                "-fx-background-color: #0F766E;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 29px;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3px;" +
                "-fx-border-radius: 29px;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                event -> {

                    try {

                        AIChatbotUI.show(
                                (Stage) button
                                        .getScene()
                                        .getWindow()
                        );

                    } catch (Exception e) {

                        e.printStackTrace();

                        showAlert(
                                "AI Assistant",
                                "AI Assistant is not available."
                        );
                    }
                }
        );

        return button;
    }

    // =========================================================
    // ALERT
    // =========================================================

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
}