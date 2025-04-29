package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.PlayerController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.web.client.RestClient;

public class LeaveGameDialog {
    private final GameController gameController;
    private final Game game;
    private final Runnable onLeaveGameComplete;
    private final Alert leaveGameAlert;

    public LeaveGameDialog(GameController gameController, Game game, Runnable onLeaveGameComplete) {
        this.gameController = gameController;
        this.game = game;
        this.onLeaveGameComplete = onLeaveGameComplete;
        this.leaveGameAlert = new Alert(Alert.AlertType.CONFIRMATION);
        this.leaveGameAlert.initModality(Modality.APPLICATION_MODAL);

        leaveGameAlert.setTitle("Leave Game");
        leaveGameAlert.setHeaderText("Are you sure you want to leave the game: " + game.getName() + "?");
        leaveGameAlert.setContentText("You will no longer be a participant in this game.");
    }

    public void show() {
        leaveGameAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    //gameController.leaveGame(game); // Call the controller to handle leaving the game
                    onLeaveGameComplete.run(); // Refresh the game list
                    showAlert("Success", "You have successfully left the game.");
                } catch (Exception ex) {
                    showAlert("Error", "Failed to leave the game: " + ex.getMessage());
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
