package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperUsers;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSignUpClient extends Application {

    private VBox gameListContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        RestClient customClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        Label title = new Label("RoboRally Games");

        Button createGameButton = new Button("Create Game");
        createGameButton.setOnAction(event -> showCreateGameDialog(customClient));

        gameListContainer = new VBox();
        gameListContainer.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gameListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250); // Adjust height as needed

        root.getChildren().addAll(title, createGameButton, scrollPane);

        refreshGameList(customClient);

        primaryStage.setTitle("RoboRally Game Sign-Up");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
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

        TextField userField = new TextField();
        userField.setPromptText("Enter your user");

        TextField playerField = new TextField();
        playerField.setPromptText("Enter your player");

        Button submitButton = new Button("Sign up!");
        submitButton.setOnAction(e -> {
            try {
                String userName = userField.getText();
                String playerName = playerField.getText();

                List<User> users = client.get()
                        .uri("/user")
                        .retrieve()
                        .body(HALWrapperUsers.class)
                        .getUsers();

                User user = users.stream()
                        .filter(u -> userName.equalsIgnoreCase(u.getName()))
                        .findFirst()
                        .orElse(null);

                if (user == null) {
                    User newUser = new User();
                    newUser.setName(userName);
                    client.post().uri("/user").body(newUser).retrieve().toBodilessEntity();

                    user = client.get()
                            .uri("/user")
                            .retrieve()
                            .body(HALWrapperUsers.class)
                            .getUsers()
                            .stream()
                            .filter(u -> userName.equalsIgnoreCase(u.getName()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Failed to create and retrieve user"));
                }

                Map<String, Object> playerData = new HashMap<>();
                playerData.put("name", playerName);
                playerData.put("user", "/user/" + user.getUid());
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
        layout.add(new Label("User:"), 0, 1);
        layout.add(userField, 1, 1);
        layout.add(new Label("Player:"), 0, 2);
        layout.add(playerField, 1, 2);
        layout.add(submitButton, 1, 3);

        signUpStage.setScene(new Scene(layout));
        signUpStage.sizeToScene();
        signUpStage.show();
    }
}
