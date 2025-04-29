package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.GameController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateGameDialog {
    private final GameController gameController;
    private final Runnable onGameCreated;
    private final Stage dialog;

    public CreateGameDialog(GameController gameController, Runnable onGameCreated) {
        this.gameController = gameController;
        this.onGameCreated = onGameCreated;
        this.dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Game");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel = new Label("Game Name:");
        TextField nameField = new TextField();

        Label minPlayersLabel = new Label("Min Players:");
        Spinner<Integer> minPlayersSpinner = new Spinner<>(1, 10, 2);
        minPlayersSpinner.setEditable(true);

        Label maxPlayersLabel = new Label("Max Players:");
        Spinner<Integer> maxPlayersSpinner = new Spinner<>(2, 20, 6);
        maxPlayersSpinner.setEditable(true);

        Button createButton = new Button("Create");
        Button cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(createButton, cancelButton);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(minPlayersLabel, 0, 1);
        grid.add(minPlayersSpinner, 1, 1);
        grid.add(maxPlayersLabel, 0, 2);
        grid.add(maxPlayersSpinner, 1, 2);
        grid.add(buttonBox, 1, 3);

        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            int minPlayers = minPlayersSpinner.getValue();
            int maxPlayers = maxPlayersSpinner.getValue();

            if (name.isEmpty()) {
                showAlert("Error", "Game name cannot be empty");
                return;
            }

            if (minPlayers > maxPlayers) {
                showAlert("Error", "Min players cannot be greater than max players");
                return;
            }

            try {
                gameController.createGame(name, minPlayers, maxPlayers);
                showAlert("Success", "Game created successfully");
                onGameCreated.run();
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", "Failed to create game: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 350, 200);
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