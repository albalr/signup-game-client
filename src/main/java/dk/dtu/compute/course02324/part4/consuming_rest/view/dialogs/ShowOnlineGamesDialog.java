package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.PlayerController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.UserController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;

import dk.dtu.compute.course02324.part4.consuming_rest.view.MainView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class ShowOnlineGamesDialog {
    private final GameController gameController;
    private final UserController userController;
    private final PlayerController playerController;
    private final Stage dialog;
    private VBox gameList;
    private ScrollPane scrollPane;

    public ShowOnlineGamesDialog(GameController gameController, UserController userController, PlayerController playerController) {
        this.gameController = gameController;
        this.userController = userController;
        this.playerController = playerController;
        this.dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Open Games Available to Join");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Button createGameButton = new Button("Create Game");
        createGameButton.setDisable(userController.getCurrentUser() == null);
        createGameButton.setOnAction(e -> {
            new CreateGameDialog(gameController, userController, playerController, this::refreshDialog).show();
        });

        Label descriptionLabel = new Label("Games currently available to join:");
        descriptionLabel.setStyle("-fx-font-weight: bold;");

        gameList = new VBox(10);
        scrollPane = new ScrollPane(gameList);
        scrollPane.setFitToWidth(true);

        root.getChildren().addAll(descriptionLabel, createGameButton, scrollPane, createCloseButton());
        refreshGameList();

        Scene scene = new Scene(root, 500, 500);
        dialog.setScene(scene);
    }

    private void refreshDialog() {
        refreshGameList();
    }

    private void refreshGameList() {
        gameList.getChildren().clear();
        List<Game> openGames = gameController.getOpenGames();

        if (openGames.isEmpty()) {
            Label noGamesLabel = new Label("No open games available to join");
            noGamesLabel.setStyle("-fx-font-style: italic;");
            gameList.getChildren().add(noGamesLabel);
        } else {
            for (Game game : openGames) {
                VBox gameBox = new VBox(5);
                gameBox.setPadding(new Insets(10));
                gameBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

                Label nameLabel = new Label("Game: " + game.getName());
                nameLabel.setStyle("-fx-font-weight: bold;");
                Label playersLabel = new Label("Players: " + game.getMinPlayers() + "-" + game.getMaxPlayers());
                Label ownerLabel = new Label("Owner: " + game.getOwner());

                int currentPlayers = game.getPlayers() != null ? game.getPlayers().size() : 0;
                Label currentPlayersLabel = new Label(String.format("Current players: %d/%d", currentPlayers, game.getMaxPlayers()));

                Button joinButton = new Button("Join");
                joinButton.setDisable(userController.getCurrentUser() == null ||
                        playerController.isPlayerInGame(game, userController.getCurrentUser()) ||
                        currentPlayers >= game.getMaxPlayers());
                joinButton.setOnAction(e -> {
                    new GameSignUpDialog(playerController, game, this::refreshDialog).show();
                });

                Button leaveButton = new Button("Leave");
                leaveButton.setDisable(userController.getCurrentUser() == null ||
                        !playerController.isPlayerInGame(game, userController.getCurrentUser()) ||
                        userController.getCurrentUser().getName().equals(game.getOwner()));
                leaveButton.setOnAction(e -> {
                    new LeaveGameDialog(gameController, game, this::refreshDialog, userController).show();
                });

                Button startButton = new Button("Start");
                startButton.setDisable(!gameController.canStartGame(game));
                startButton.setOnAction(e -> {
                    try {
                        gameController.startGame(game);
                        refreshDialog();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Could not start game:\n" + ex.getMessage(), ButtonType.OK);
                        alert.showAndWait();
                    }
                });

                Button deleteButton = new Button("Delete");
                deleteButton.setDisable(userController.getCurrentUser() == null ||
                        !Objects.equals(userController.getCurrentUser().getName(), game.getOwner()));
                deleteButton.setOnAction(e -> {
                    new DeleteGameDialog(gameController, game, this::refreshDialog).show();
                });

                HBox buttons = new HBox(10, joinButton, leaveButton, startButton, deleteButton);
                gameBox.getChildren().addAll(nameLabel, playersLabel, currentPlayersLabel, ownerLabel, buttons);
                gameList.getChildren().add(gameBox);
            }
        }
    }

    private Button createCloseButton() {
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());
        return closeButton;
    }

    public void show() {
        dialog.showAndWait();
    }
}