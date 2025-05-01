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
        dialog.setTitle("Open Games");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane();
        VBox gameList = new VBox(10);
        scrollPane.setContent(gameList);
        scrollPane.setFitToWidth(true);

        List<Game> openGames = gameController.getOpenGames();

        for (Game game : openGames) {
            VBox gameBox = new VBox(5);
            gameBox.setPadding(new Insets(10));
            gameBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

            Label nameLabel = new Label("Game: " + game.getName());
            Label playersLabel = new Label("Players: " + game.getMinPlayers() + "-" + game.getMaxPlayers());
            Label ownerLabel = new Label("Owner: " + game.getOwner());

            Button joinButton = new Button("Join");
            Button leaveButton = new Button("Leave");
            Button startButton = new Button("Start");
            Button deleteButton = new Button("Delete");

            // Disabled for now (feature not implemented yet)
            joinButton.setDisable(true);
            leaveButton.setDisable(true);
            startButton.setDisable(true);
            deleteButton.setDisable(true);

            HBox buttonBox = new HBox(10, joinButton, leaveButton, startButton, deleteButton);
            gameBox.getChildren().addAll(nameLabel, playersLabel, ownerLabel, buttonBox);
            gameList.getChildren().add(gameBox);
        }

        root.getChildren().add(scrollPane);
        Scene scene = new Scene(root, 400, 400);
        dialog.setScene(scene);
    }

    public void show() {
        dialog.showAndWait();
    }
}
