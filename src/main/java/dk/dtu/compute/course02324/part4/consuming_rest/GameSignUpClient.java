package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSignUpClient extends Application {

    private User signedInUser;
    private VBox gameListContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            RestClient customClient = RestClient.builder()
                    .baseUrl("http://localhost:8080")
                    .build();

            BorderPane root = new BorderPane();

            Menu fileMenu = new Menu("File");

            MenuItem signInItem = new MenuItem("Sign In");
            signInItem.setOnAction(e -> showSignInDialog(customClient));

            MenuItem signUpItem = new MenuItem("Sign Up");
            signUpItem.setOnAction(e -> showSignUpDialog(customClient));

            MenuItem signOutItem = new MenuItem("Sign Out");
            signOutItem.setOnAction(e -> signOut());

            fileMenu.getItems().addAll(signInItem, signUpItem, signOutItem);

            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().add(fileMenu);

            root.setTop(menuBar);

            VBox content = new VBox();
            content.setSpacing(10);
            content.setPadding(new Insets(10));

            Label title = new Label("RoboRally Games");

            Button createGameButton = new Button("Create Game");
            createGameButton.setOnAction(event -> showCreateGameDialog(customClient));

            gameListContainer = new VBox();
            gameListContainer.setSpacing(10);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(gameListContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(250);

            content.getChildren().addAll(title, createGameButton, scrollPane);

            root.setCenter(content);

            refreshGameList(customClient);

            primaryStage.setTitle("RoboRally Game Sign-Up");
            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshGameList(RestClient client) {
        gameListContainer.getChildren().clear();

        List<Game> games = client.get()
                .uri("/game")
                .retrieve()
                .body(HALWrapperGames.class)
                .getGames();

        for (Game game : games) {
            HBox gameBox = new HBox();
            gameBox.setSpacing(10);
            gameBox.setPadding(new Insets(5));

            GridPane gameInfo = new GridPane();
            gameInfo.setHgap(10);
            gameInfo.setVgap(10);
            gameInfo.setPadding(new Insets(10));

            gameInfo.add(new Label("Game Name:"), 0, 0);
            gameInfo.add(new Label(game.getName()), 1, 0);
            gameInfo.add(new Label("Min Players:"), 0, 1);
            gameInfo.add(new Label(String.valueOf(game.getMinPlayers())), 1, 1);
            gameInfo.add(new Label("Max Players:"), 0, 2);
            gameInfo.add(new Label(String.valueOf(game.getMaxPlayers())), 1, 2);

            Button signUpButton = new Button("Sign Up");
            signUpButton.setOnAction(event -> showSignUpDialog(client, game));

            VBox buttonBox = new VBox();
            buttonBox.setPadding(new Insets(30, 0, 0, 0));
            buttonBox.getChildren().add(signUpButton);

            HBox gamePane = new HBox();
            gamePane.setPrefWidth(250);
            gamePane.getChildren().addAll(gameInfo, buttonBox);

            gameBox.getChildren().add(gamePane);
            gameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: white;");

            gameListContainer.getChildren().add(gameBox);
        }
    }

    private void showCreateGameDialog(RestClient client) {
        if (signedInUser == null) {
            showAlert("Not Signed In", "Please sign in before creating a game.");
            return;
        }

        Stage createGameStage = new Stage();
        createGameStage.setTitle("Create New Game");

        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(15);
        layout.setPadding(new Insets(10));
        layout.setPrefWidth(300);

        TextField nameField = new TextField();
        TextField minPlayersField = new TextField();
        TextField maxPlayersField = new TextField();

        Button submitButton = new Button("Submit Game");
        submitButton.setOnAction(e -> {
            try {
                Game newGame = new Game();
                newGame.setName(nameField.getText());
                newGame.setMinPlayers(Integer.parseInt(minPlayersField.getText()));
                newGame.setMaxPlayers(Integer.parseInt(maxPlayersField.getText()));

                client.post()
                        .uri("/game")
                        .body(newGame)
                        .retrieve()
                        .body(Game.class);

                refreshGameList(client);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            createGameStage.close();
        });

        layout.add(new Label("Game Name:"), 0, 0);
        layout.add(nameField, 1, 0);
        layout.add(new Label("Min Players:"), 0, 1);
        layout.add(minPlayersField, 1, 1);
        layout.add(new Label("Max Players:"), 0, 2);
        layout.add(maxPlayersField, 1, 2);
        layout.add(submitButton, 1, 3);

        createGameStage.setScene(new Scene(layout));
        createGameStage.sizeToScene();
        createGameStage.show();
    }

    private void showSignUpDialog(RestClient client, Game game) {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up for Game");

        if (signedInUser == null) {
            showAlert("Not Signed In", "Please sign in before signing up for a game.");
            signUpStage.close();
            return;
        }

        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(15);
        layout.setPadding(new Insets(10));

        GridPane labelBox = new GridPane();
        labelBox.setHgap(10);
        labelBox.setVgap(15);
        labelBox.setPadding(new Insets(10));
        labelBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: white;");

        labelBox.add(new Label("Game: " + game.getName()), 0, 0);
        labelBox.add(new Label("Min players: " + game.getMinPlayers()), 0, 1);
        labelBox.add(new Label("Max players: " + game.getMaxPlayers()), 0, 2);

        TextField playerField = new TextField();
        playerField.setPromptText("Enter your player");

        Button submitButton = new Button("Sign up!");
        submitButton.setOnAction(e -> {
            try {
                String userName = signedInUser.getName();
                String playerName = playerField.getText();

                List<User> users = client.get()
                        .uri("/users/allusers")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<User>>() {});

                User user = users.stream()
                        .filter(u -> userName.equalsIgnoreCase(u.getName()))
                        .findFirst()
                        .orElse(null);

                if (user == null) {
                    User newUser = new User();
                    newUser.setName(userName);
                    client.post()
                            .uri("/users/signup")
                            .body(newUser)
                            .retrieve()
                            .toBodilessEntity();

                    users = client.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/users/searchusers")
                                    .queryParam("name", userName)
                                    .build())
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<User>>() {});
                }

                user = users.stream()
                        .filter(u -> userName.equalsIgnoreCase(u.getName()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Failed to create and retrieve user"));

                Map<String, Object> playerData = new HashMap<>();
                playerData.put("name", playerName);
                playerData.put("user", "/users/" + user.getUid());
                playerData.put("game", "/game/" + game.getUid());

                client.post()
                        .uri("/player")
                        .body(playerData)
                        .retrieve()
                        .body(Player.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            signUpStage.close();
        });

        layout.add(labelBox, 0, 0, 2, 1);
        layout.add(new Label("Player:"), 0, 1);
        layout.add(playerField, 1, 1);
        layout.add(submitButton, 1, 2);

        signUpStage.setScene(new Scene(layout));
        signUpStage.sizeToScene();
        signUpStage.show();
    }

    private void showSignInDialog(RestClient client) {
        Stage dialog = new Stage();
        dialog.setTitle("Sign In to RoboRally");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        Label instruction = new Label("Enter your username:");
        TextField userNameField = new TextField();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button signInButton = new Button("Sign in");
        Button cancelButton = new Button("Cancel");

        signInButton.setOnAction(e -> {
            String name = userNameField.getText().trim();
            if (name.length() < 4) {
                errorLabel.setText("Username must be at least 4 characters long");
                return;
            }

            try {
                List<User> users = client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/users/searchusers")
                                .queryParam("name", name)
                                .build())
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<User>>() {});

                if (!users.isEmpty()) {
                    // Existing user found
                    signedInUser = users.get(0);
                    System.out.println("Signed in as: " + signedInUser.getName());
                    showAlert("Success", "Successfully signed in as " + signedInUser.getName());
                    dialog.close();
                } else {
                    errorLabel.setText("User not found. Please sign up first.");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error connecting to server. Please try again.");
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        grid.add(instruction, 0, 0, 2, 1);
        grid.add(userNameField, 0, 1, 2, 1);
        grid.add(errorLabel, 0, 2, 2, 1);
        grid.add(signInButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showSignUpDialog(RestClient client) {
        Stage dialog = new Stage();
        dialog.setTitle("Sign Up for RoboRally");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        Label instruction = new Label("Enter your username (minimum 4 characters):");
        TextField userNameField = new TextField();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button signUpButton = new Button("Sign up");
        Button cancelButton = new Button("Cancel");

        signUpButton.setOnAction(e -> {
            String name = userNameField.getText().trim();
            if (name.length() < 4) {
                errorLabel.setText("Username must be at least 4 characters long");
                return;
            }

            try {
                List<User> users = client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/users/searchusers")
                                .queryParam("name", name)
                                .build())
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<User>>() {});

                if (!users.isEmpty()) {
                    errorLabel.setText("Username already exists. Please choose a different name.");
                } else {
                    // Create new user
                    User newUser = new User();
                    newUser.setName(name);
                    try {
                        User createdUser = client.post()
                                .uri("/users/signup")
                                .body(newUser)
                                .retrieve()
                                .body(User.class);
                        
                        signedInUser = createdUser;
                        System.out.println("Created and signed in as: " + signedInUser.getName());
                        showAlert("Success", "Successfully created and signed in as " + signedInUser.getName());
                        dialog.close();
                    } catch (Exception ex) {
                        errorLabel.setText("Failed to create new user. Please try again.");
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                errorLabel.setText("Error connecting to server. Please try again.");
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        grid.add(instruction, 0, 0, 2, 1);
        grid.add(userNameField, 0, 1, 2, 1);
        grid.add(errorLabel, 0, 2, 2, 1);
        grid.add(signUpButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void signOut() {
        if (signedInUser != null) {
            System.out.println("Signed out user: " + signedInUser.getName());
        }
        signedInUser = null;
        showAlert("Signed Out", "You have been signed out.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
