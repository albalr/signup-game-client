package dk.dtu.compute.course02324.part4.consuming_rest.view;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.PlayerController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.UserController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.RestApiService;
import dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.util.List;

public class MainView {
    private final RestApiService apiService;
    private final UserController userController;
    private final GameController gameController;
    private final PlayerController playerController;

    private VBox gameListContainer;

    public MainView() {
        this.apiService = new RestApiService();
        this.userController = new UserController(apiService);
        this.gameController = new GameController(apiService);
        this.playerController = new PlayerController(apiService, userController);
    }

    public void show(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Build menu
        MenuBar menuBar = createMenuBar(userController, gameController, playerController);
        root.setTop(menuBar);

        // Main content
        VBox content = createContent();
        root.setCenter(content);

        // Load initial data
        refreshGameList();

        primaryStage.setTitle("RoboRally Game Sign-Up");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static MenuBar createMenuBar(UserController userController, GameController gameController, PlayerController playerController) {
        Menu fileMenu = new Menu("File");

        MenuItem signInItem = new MenuItem("Sign In");
        signInItem.setOnAction(e -> new SignInDialog(userController).show());

        MenuItem signUpItem = new MenuItem("Sign Up");
        signUpItem.setOnAction(e -> new SignUpDialog(userController).show());

        MenuItem signOutItem = new MenuItem("Sign Out");
        signOutItem.setOnAction(e -> {
            userController.signOut();
            showAlert("Signed Out", "You have been signed out.");
        });

        MenuItem sogItem = new MenuItem("Show Online Games");
        sogItem.setOnAction(e -> {
            if (userController.getCurrentUser() == null) {
                showAlert("Not Signed In", "Please sign in to view online games.");
            } else {
                new ShowOnlineGamesDialog(gameController).show();
            }
        });

        fileMenu.getItems().add(sogItem);


        fileMenu.getItems().addAll(signInItem, signUpItem, signOutItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private VBox createContent() {
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        Label title = new Label("RoboRally Games");

        Button createGameButton = new Button("Create Game");
        createGameButton.setOnAction(e -> {
            if (userController.getCurrentUser() == null) {
                showAlert("Not Signed In", "Please sign in before creating a game.");
                return;
            }
            new CreateGameDialog(gameController, this::refreshGameList).show();
        });

        gameListContainer = new VBox();
        gameListContainer.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gameListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);

        content.getChildren().addAll(title, createGameButton, scrollPane);

        return content;
    }

    public void refreshGameList() {
        gameListContainer.getChildren().clear();

        List<Game> games = gameController.getAllGames();

        for (Game game : games) {
            HBox gameBox = createGameListItem(game);
            gameListContainer.getChildren().add(gameBox);
        }
    }

    private HBox createGameListItem(Game game) {
        HBox gameBox = new HBox();
        gameBox.setSpacing(10);
        gameBox.setPadding(new Insets(5));
        gameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: white;");

        Label nameLabel = new Label(game.getName());
        Label playersLabel = new Label("Players: " + game.getMinPlayers() + "-" + game.getMaxPlayers());

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(event -> {
            if (userController.getCurrentUser() == null) {
                showAlert("Not Signed In", "Please sign in before signing up for a game.");
                return;
            }
            new GameSignUpDialog(playerController, game, this::refreshGameList).show();
        });

        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(nameLabel, playersLabel);

        gameBox.getChildren().addAll(infoBox, signUpButton);

        return gameBox;
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}