package dk.dtu.compute.course02324.part4.consuming_rest.view.dialogs;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.UserController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
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

public class SignInDialog {
    private final UserController userController;
    private final Runnable onSignInComplete;
    private final Stage dialog;

    public SignInDialog(UserController userController, Runnable onSignInComplete) {
        this.userController = userController;
        this.onSignInComplete = onSignInComplete;
        this.dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Sign In");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Button signInButton = new Button("Sign In");
        Button cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(signInButton, cancelButton);

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(buttonBox, 1, 1);

        signInButton.setOnAction(e -> {
            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                showAlert("Error", "Username cannot be empty");
                return;
            }

            User user = userController.signIn(username);

            if (user != null) {
                showAlert("Success", "Signed in as " + user.getName());
                onSignInComplete.run();
                dialog.close();
            } else {
                showAlert("Error", "User not found");
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 300, 120);
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