package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.PlayerController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameSignUpDialog {
    private final PlayerController playerController;
    private final Game game;
    private final Runnable onSignUpComplete;
    private final Stage dialog;

    public GameSignUpDialog(PlayerController playerController, Game game, Runnable onSignUpComplete) {
        this.playerController = playerController;
        this.game = game;
        this.onSignUpComplete = onSignUpComplete;
        this.dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Sign Up for Game");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label gameNameLabel = new Label("Game:");
        Label gameNameValue = new Label(game.getName());
        
        String currentUsername = playerController.getCurrentUsername();
        
        Label playerNameLabel = new Label("Player Name:");
        TextField playerNameField = new TextField(currentUsername);

        Button signUpButton = new Button("Sign Up");
        Button cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(signUpButton, cancelButton);

        grid.add(gameNameLabel, 0, 0);
        grid.add(gameNameValue, 1, 0);
        grid.add(playerNameLabel, 0, 1);
        grid.add(playerNameField, 1, 1);
        grid.add(buttonBox, 1, 2);

        signUpButton.setOnAction(e -> {
            String playerName = playerNameField.getText().trim();

            if (playerName.isEmpty()) {
                showAlert("Error", "Player name cannot be empty");
                return;
            }

            try {
                playerController.signUpForGame(game, playerName);
                showAlert("Success", "Signed up for game successfully");
                onSignUpComplete.run();
                dialog.close();
            } catch (Exception ex) {
                String errorMessage = ex.getMessage();
                if (errorMessage != null && errorMessage.contains("already joined")) {
                    showAlert("Already Joined", "You've already joined this game as a player");
                    onSignUpComplete.run();
                    dialog.close();
                } else {
                    showAlert("Error", "Failed to sign up for game: " + errorMessage);
                }
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 350, 150);
        dialog.setScene(scene);
    }

    public void show() {
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}