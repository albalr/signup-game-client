package dk.dtu.compute.course02324.part4.consuming_rest.view;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.PlayerController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.RestApiService;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.UserController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs.ShowOnlineGamesDialog;
import dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs.SignInDialog;
import dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs.SignUpDialog;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainView {
    private final RestApiService apiService;
    private final UserController userController;
    private final GameController gameController;
    private final PlayerController playerController;

    private VBox gameListContainer;
    private Label userStatusLabel;
    
    public MainView() {
        this.apiService = new RestApiService();
        this.userController = new UserController(apiService);
        this.gameController = new GameController(apiService, userController.getCurrentUser());
        this.playerController = new PlayerController(apiService, userController);
    }

    public void show(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // user status label
        userStatusLabel = new Label();
        userStatusLabel.setPadding(new Insets(5));
        userStatusLabel.setStyle("-fx-font-weight: bold;");
        updateUserStatus();

        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(createMenuBar(), userStatusLabel);
        root.setTop(topContainer);

        // Main content
        VBox content = createContent();
        root.setCenter(content);

        primaryStage.setTitle("RoboRally Game Sign-Up");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
        
    }

    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu("File");

        MenuItem signInItem = new MenuItem("Sign In");
        signInItem.setOnAction(e -> {
            if (userController.getCurrentUser() != null) {
                showAlert("Already Signed In", "You must sign out before signing in with a different account.");
                return;
            }
            new SignInDialog(userController, () -> {
                updateUserStatus();
                refreshGameList();
            }).show();
        });


        MenuItem signUpItem = new MenuItem("Sign Up");
        signUpItem.setOnAction(e -> {
            if (userController.getCurrentUser() != null) {
                showAlert("Already Signed In", "You must sign out before signing up with a new account.");
                return;
            }
            new SignUpDialog(userController, () -> {
                updateUserStatus();
                refreshGameList();
            }).show();
        });

        MenuItem signOutItem = new MenuItem("Sign Out");
        signOutItem.setOnAction(e -> {
            if (userController.getCurrentUser() == null) {
                showAlert("Error", "You are already signed out!");
                return;
            }
            userController.signOut();
            showAlert("Success", "You have been signed out.");
            updateUserStatus();
            refreshGameList();
        });
        
        // SOG
        MenuItem showOnlineGamesItem = new MenuItem("Show Games Available to Join");
        showOnlineGamesItem.setOnAction(e -> {
            if (userController.getCurrentUser() == null) {
                showAlert("Error", "Please sign in to view available games");
                return;
            }
            new ShowOnlineGamesDialog(gameController, userController, playerController).show();
            refreshGameList();
        });

        fileMenu.getItems().addAll(signInItem, signUpItem, signOutItem, new SeparatorMenuItem(), showOnlineGamesItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private VBox createContent() {
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        Label title = new Label("RoboRally Games");

        gameListContainer = new VBox();
        gameListContainer.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gameListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);

        content.getChildren().addAll(title, scrollPane);

        return content;
    }

    public void refreshGameList() {
        gameListContainer.getChildren().clear();

        if (userController.getCurrentUser() == null) {
            return;
        }

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
        
        // simple border only to separate items
        gameBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label nameLabel = new Label(game.getName());
        Label playersLabel = new Label("Players: " + game.getMinPlayers() + "-" + game.getMaxPlayers());
        Label statusLabel = new Label("Status: " + game.getStatus());
        Label hostLabel = new Label("Host: " + game.getOwner());
        
        // join status label
        User currentUser = userController.getCurrentUser();
        String joinStatus = "Not joined";

        // special status for host
        boolean isUserHost = gameController.isUserHost(game, currentUser);
                             
        if (isUserHost) {
            joinStatus = "Host";
        } else if (currentUser != null && playerController.isPlayerInGame(game, currentUser)) {
            joinStatus = "Joined";
        }

        Label joinStatusLabel = new Label(joinStatus);

        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(nameLabel, playersLabel, statusLabel, hostLabel, joinStatusLabel);

        // create a VBox for the action buttons and player list
        VBox actionsAndPlayers = new VBox(5);
        HBox buttonsBox = new HBox(5);

        // create a simple VBox to display players instead of ListView
        VBox playerListBox = new VBox(3);
        Label playersJoinedLabel = new Label("Joined Players:");
        playerListBox.getChildren().add(playersJoinedLabel);
        
        // add all players to the list
        if (game.getPlayers() != null && !game.getPlayers().isEmpty()) {
            for (Player player : game.getPlayers()) {
                String playerName = playerController.getDisplayablePlayerName(player);
                
                if (playerName != null && !playerName.isEmpty() && !playerName.equals("null")) {
                    // check if this player is the host/owner
                    boolean isOwner = playerName.equals(game.getOwner());
                    String displayName = playerName + (isOwner ? " (Host)" : "");
                    Label playerLabel = new Label("- " + displayName);
                    playerListBox.getChildren().add(playerLabel);
                }
            }
        }
        
        // only add the owner separately if they're not already in the list
        boolean ownerInList = false;
        if (game.getPlayers() != null) {
            ownerInList = game.getPlayers().stream()
                .anyMatch(p -> (p.getName() != null && p.getName().equals(game.getOwner())) || 
                             (p.getUser() != null && p.getUser().getName() != null && 
                              p.getUser().getName().equals(game.getOwner())));
        }

        if (!ownerInList && game.getOwner() != null) {
            Label ownerLabel = new Label("- " + game.getOwner() + " (Host)");
            playerListBox.getChildren().add(ownerLabel);
        }

        // add buttons based on game status
        if (game.getStatus() == Game.GameStatus.SIGNUP) {
            actionsAndPlayers.getChildren().addAll(buttonsBox, playerListBox);
            gameBox.getChildren().addAll(infoBox, actionsAndPlayers);
        } else {
            // for ACTIVE or FINISHED games, just show info
            Button playButton = new Button("Play");
            playButton.setDisable(true); // Not implemented yet
            playButton.setTooltip(new Tooltip("Not implemented yet"));

            if (game.getStatus() == Game.GameStatus.ACTIVE &&
                (playerController.isPlayerInGame(game, currentUser) || gameController.isUserHost(game, currentUser))) {
                playButton.setDisable(false);
                playButton.setTooltip(new Tooltip("Enter the game"));

                // Add event handler for Play button
                playButton.setOnAction(event -> {
                    Alert launchDialog = new Alert(Alert.AlertType.INFORMATION);
                    launchDialog.setTitle("Launching RoboRally");
                    launchDialog.setHeaderText(null);
                    launchDialog.setContentText("Launching RoboRally... [Not implemented; outside the scope of this course]");
                    launchDialog.showAndWait();
                });
            }

            buttonsBox.getChildren().add(playButton);
            actionsAndPlayers.getChildren().addAll(buttonsBox, playerListBox);
            gameBox.getChildren().addAll(infoBox, actionsAndPlayers);
        }

        return gameBox;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateUserStatus() {
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            userStatusLabel.setText("Signed in as: " + currentUser.getName());
            gameController.setCurrentUser(currentUser);
        } else {
            userStatusLabel.setText("Not signed in");
            gameController.setCurrentUser(null);
        }
    }
}