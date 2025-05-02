package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import dk.dtu.compute.course02324.part4.consuming_rest.controller.UserController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LeaveGameDialog {
    private final GameController gameController;
    private final Game game;
    private final Runnable onLeaveGameComplete;
    private final Alert leaveGameAlert;
    private final UserController userController;

    public LeaveGameDialog(GameController gameController, Game game, Runnable onLeaveGameComplete, UserController userController) {
        this.gameController = gameController;
        this.game = game;
        this.onLeaveGameComplete = onLeaveGameComplete;
        this.leaveGameAlert = new Alert(Alert.AlertType.CONFIRMATION);
        this.leaveGameAlert.initModality(Modality.APPLICATION_MODAL);
        this.userController = userController;

        leaveGameAlert.setTitle("Leave Game");
        leaveGameAlert.setHeaderText("Leave Game Confirmation");
        leaveGameAlert.setContentText("Are you sure you want to leave the game '" + game.getName() + "'?");
    }

    public void show() {
        if (userController.getCurrentUser() == null) {
            showAlert("Not Signed In", "Please sign in before leaving a game.");
            return;
        }

        leaveGameAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Game updatedGame = gameController.leaveGame(game);
                    onLeaveGameComplete.run();
                    showAlert("Success", "You have left the game.");
                } catch (IllegalStateException e) {
                    showAlert("Error", e.getMessage());
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
