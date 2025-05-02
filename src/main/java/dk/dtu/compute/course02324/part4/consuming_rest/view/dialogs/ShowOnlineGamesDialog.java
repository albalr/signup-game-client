package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ShowOnlineGamesDialog {
    private final GameController gameController;
    private final Stage dialog;

    public ShowOnlineGamesDialog(GameController gameController) {
        this.gameController = gameController;
        this.dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Open Games Available to Join");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label descriptionLabel = new Label("Games currently available to join:");
        descriptionLabel.setStyle("-fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        VBox gameList = new VBox(10);
        scrollPane.setContent(gameList);
        scrollPane.setFitToWidth(true);

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
                Label currentPlayersLabel = new Label(String.format("Current players: %d/%d", 
                                                                currentPlayers, 
                                                                game.getMaxPlayers()));

                Button joinButton = new Button("Join");
                Button leaveButton = new Button("Leave");
                Button startButton = new Button("Start");
                Button deleteButton = new Button("Delete");

                joinButton.setDisable(true);
                leaveButton.setDisable(true);
                startButton.setDisable(true);
                deleteButton.setDisable(true);

                HBox buttonBox = new HBox(10, joinButton, leaveButton, startButton, deleteButton);
                gameBox.getChildren().addAll(nameLabel, playersLabel, currentPlayersLabel, ownerLabel, buttonBox);
                gameList.getChildren().add(gameBox);
            }
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());

        root.getChildren().addAll(descriptionLabel, scrollPane, closeButton);
        Scene scene = new Scene(root, 450, 400);
        dialog.setScene(scene);
    }

    public void show() {
        dialog.showAndWait();
    }
} 