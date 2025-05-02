package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.PlayerController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.web.client.RestClient;

public class DeleteGameDialog {
    private final GameController gameController;
    private final Game game;
    private final Runnable onLeaveGameComplete;
    private final Alert deleteGameAlert;

    public DeleteGameDialog(GameController gameController, Game game, Runnable onLeaveGameComplete) {
        this.gameController = gameController;
        this.game = game;
        this.onLeaveGameComplete = onLeaveGameComplete;
        this.deleteGameAlert = new Alert(Alert.AlertType.CONFIRMATION);
        this.deleteGameAlert.initModality(Modality.APPLICATION_MODAL);

        deleteGameAlert.setTitle("Delete Game");
        deleteGameAlert.setHeaderText("Are you sure you want to delete the game: " + game.getName() + "?");
        deleteGameAlert.setContentText("This will remove the game and all its players. This action cannot be undone.");
    }

    public void show() {
        deleteGameAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    gameController.deleteGame(game);
                    onLeaveGameComplete.run();
                    showAlert("Success", "You have successfully deleted the game.");
                } catch (Exception ex) {
                    showAlert("Error", "Failed to delete the game: " + ex.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
