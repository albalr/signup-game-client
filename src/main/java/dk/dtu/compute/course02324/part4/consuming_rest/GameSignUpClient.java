package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperPlayers;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperUsers;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

public class GameSignUpClient extends Application {

    // see
    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-resttemplate

    public static void main(String[] args) {

        launch(args);

        /*
         * Before you start this make sure you have created a player (with uid=1) and
         * a game (with uid=1) in the database; you can do that via the command
         * line tool curl, the tool Postman or the HAL explorer (which, after
         * your have started your backend is available at http://localhost:8080/api).
         *
         * You can create a player by posting
         *
         * {
         * "maxPlayers": 2,
         * "minPlayers": 6,
         * "name": "First Game"
         * }
         *
         * to http://localhost:8080/game
         *
         * and by posting
         *
         * {
         * "name": "Player 1"
         * }
         *
         * to ttp://localhost:8080/game
         *
         */

        /*
         * RestClient customClient = RestClient.builder().
         * // requestFactory(new HttpComponentsClientHttpRequestFactory()).
         * baseUrl("http://localhost:8080").
         * build();
         * 
         * // String result =
         * customClient.get().uri("/game").retrieve().body(String.class);
         * String result = customClient.get().uri("/").retrieve().body(String.class);
         * 
         * System.out.println(result);
         * 
         * System.out.println(
         * "---------------------------------------------------------");
         * 
         * result = customClient.get().uri("/game").retrieve().body(String.class);
         * 
         * System.out.println(result);
         * 
         * System.out.println(
         * "---------------------------------------------------------");
         * 
         * 
         * Game game1 = customClient.get().uri("/game/1").retrieve().body(Game.class);
         * 
         * System.out.println("Game with uid 1 is: " + game1);
         * 
         * System.out.println(
         * "---------------------------------------------------------");
         * 
         * List<Game> games =
         * customClient.get().uri("/game").retrieve().body(HALWrapperGames.class).
         * getGames();
         * 
         * for (Game game: games) {
         * System.out.println(game);
         * }
         * 
         * System.out.println(
         * "---------------------------------------------------------");
         * 
         * Player player1 =
         * customClient.get().uri("/player/1").retrieve().body(Player.class);
         * 
         * System.out.println("Player with uid 1 is: " + player1);
         * 
         * 
         * System.out.println(
         * "---------------------------------------------------------");
         * 
         * // the following put request will connect game1 with player1:
         * 
         * String body = "http://localhost:8080/game/1";
         * 
         * ResponseEntity<Player> playerResponseEntity =
         * customClient.put().uri("/player/1/game").
         * header("Content-Type", "text/uri-list").
         * body(body).retrieve().toEntity(Player.class);
         * System.out.println("player: " + playerResponseEntity.toString());
         * 
         * 
         * System.out.println(
         * "---------------------------------------------------------");
         * 
         * game1 = customClient.get().uri("/player/1/game").retrieve().body(Game.class);
         * 
         * System.out.println("Game attached to Player with uid 1 is: " + game1);
         */

        // TODO try to read out the available games from the backend, show them on a
        // simple graphical GUI and sign up for a game using some of the operations
        // at the top.
        // For the GUI to work in JavaFX, you need to add some maven dependencies
        // (see pom file for Assignment 3).

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Label title = new Label("RoboRally Games");
        // ListView<String> gameList = new ListView<>();
        // Button addGameBtn = new Button("Add New Game");
        // Button signUpBtn = new Button("Sign Up as Player");

        // VBox vbox1 = new VBox(addGameBtn, gameList);
        // VBox vbox2 = new VBox(signUpBtn);
        // HBox root = new HBox(vbox1, vbox2);

        // root.setPadding(new Insets(10));

        RestClient customClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();

        List<Game> games = customClient.get()
                .uri("/game")
                .retrieve()
                .body(HALWrapperGames.class)
                .getGames();

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        Label title = new Label("RoboRally Games");

        // Create Game Button
        Button createGameButton = new Button("Create Game");
        createGameButton.setOnAction(event -> {
            System.out.println("Create Game button clicked");
            // Add functionality to create a new game

            Stage createGameStage = new Stage();
            createGameStage.setTitle("Create New Game");

            GridPane createGameLayout = new GridPane();
            createGameLayout.setHgap(10); // Space between label and text field
            createGameLayout.setVgap(15); // Space between rows
            createGameLayout.setPadding(new Insets(10));
            createGameLayout.setPrefWidth(300);

            Label nameLabel = new Label("Game Name:");
            TextField nameField = new TextField();

            Label minPlayersLabel = new Label("Min Players:");
            TextField minPlayersField = new TextField();

            Label maxPlayersLabel = new Label("Max Players:");
            TextField maxPlayersField = new TextField();

            Button submitGameButton = new Button("Submit Game");
            submitGameButton.setOnAction(e -> {
                String gameName;
                try {
                    gameName = nameField.getText();
                    if (nameField.getText() == null || nameField.getText().isEmpty()) {
                        // Show error message
                        System.out.println("Game name cannot be empty or null");
                        return;
                    }
                } catch (Exception ex) {
                    // Handle unexpected errors
                    System.out.println("An unexpected error occurred: " + ex.getMessage());
                    return;
                }

                int minPlayers;
                try {
                    minPlayers = Integer.parseInt(minPlayersField.getText());
                    if (minPlayersField.getText() == null || minPlayersField.getText().isEmpty()) {
                        // Show error message
                        System.out.println("Min players cannot be empty or null");
                        return;
                    }
                    if (minPlayers <= 0) {
                        // Show error message
                        System.out.println("Min players must be greater than 0");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    // Show error message
                    System.out.println("Min players must be a number");
                    return;
                } catch (Exception ex) {
                    // Handle unexpected errors
                    System.out.println("An unexpected error occurred: " + ex.getMessage());
                    return;
                }

                int maxPlayers;
                try {
                    maxPlayers = Integer.parseInt(maxPlayersField.getText());
                    if (maxPlayersField.getText() == null || maxPlayersField.getText().isEmpty()) {
                        // Show error message
                        System.out.println("Max players cannot be empty or null");
                        return;
                    }
                    if (maxPlayers <= 0) {
                        // Show error message
                        System.out.println("Max players must be greater than 0");
                        return;
                    }
                    if (maxPlayers < minPlayers) {
                        // Show error message
                        System.out.println("Max players must be greater than Min players");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    // Show error message
                    System.out.println("Max players must be a number");
                    return;
                } catch (Exception ex) {
                    // Handle unexpected errors
                    System.out.println("An unexpected error occurred: " + ex.getMessage());
                    return;
                }

                System.out.println("Game Created: " + gameName + ", Min Players: " + minPlayers + ", Max Players: " + maxPlayers);

                // TODO: Implement the game creation logic here

                createGameStage.close();
            });

            createGameLayout.add(nameLabel, 0, 0);
            createGameLayout.add(nameField, 1, 0);
            createGameLayout.add(minPlayersLabel, 0, 1);
            createGameLayout.add(minPlayersField, 1, 1);
            createGameLayout.add(maxPlayersLabel, 0, 2);
            createGameLayout.add(maxPlayersField, 1, 2);
            createGameLayout.add(submitGameButton, 1, 3);
            //createGameLayout.getChildren().addAll(nameLabel, nameField, minPlayersLabel, minPlayersField, maxPlayersLabel, maxPlayersField, submitGameButton);

            Scene createGameScene = new Scene(createGameLayout);
            createGameStage.setScene(createGameScene);
            createGameStage.sizeToScene();
            createGameStage.show();
        });

        root.getChildren().addAll(title, createGameButton);

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

            // gameInfo.addRow(0, "Min Players: " + game.getMinPlayers());
            // gameInfo.addRow(1, "Max Players: " + game.getMaxPlayers());
            // gameInfo.setPrefWidth(150);
            // gameInfo.setPrefHeight(50);

            Button signUpButton = new Button("Sign Up");

            signUpButton.setOnAction(event -> {
                System.out.println("Sign Up button clicked for game: " + game.getName());

                Stage signUpStage = new Stage();
                signUpStage.setTitle("Sign Up for Game");

                GridPane signUpLayout = new GridPane();
                signUpLayout.setHgap(10);
                signUpLayout.setVgap(15);
                signUpLayout.setPadding(new Insets(10));

                GridPane labelContainer = new GridPane();
                labelContainer.setHgap(10);
                labelContainer.setVgap(15);
                labelContainer.setPadding(new Insets(10));
                labelContainer.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: white;");

                Label gameNameLabel = new Label("Game: " + game.getName());
                Label minPlayersLabel = new Label("Min players: " + game.getMinPlayers());
                Label maxPlayersLabel = new Label("Max players: " + game.getMaxPlayers());
                // Not implemented yet
                // Label currentPlayers = new Label("Current players: " + game.getCurrentPlayers());

                labelContainer.add(gameNameLabel, 0, 0);
                labelContainer.add(minPlayersLabel, 0, 1);
                labelContainer.add(maxPlayersLabel, 0, 2);
                // labelContainer.add(currentPlayers, 0, 3);

                Label userNameLabel = new Label("User:");
                TextField userNameField = new TextField();
                userNameField.setPromptText("Enter your user");
                userNameField.setPrefWidth(100);

                Label playerNameLabel = new Label("Player:");
                TextField playerNameField = new TextField();
                playerNameField.setPromptText("Enter your player");
                playerNameField.setPrefWidth(100);

                Button submitSignupButton = new Button("Sign up!");
                submitSignupButton.setOnAction(e -> {
                    String userName;
                    try {
                        userName = userNameField.getText();
                        if (userName == null || userName.isEmpty()) {
                            System.out.println("User name cannot be empty or null");
                            return;
                        }
                    } catch (Exception ex) {
                        System.out.println("An unexpected error occurred: " + ex.getMessage());
                        return;
                    }

                    String playerName;
                    try {
                        playerName = playerNameField.getText();
                        if (playerName == null || playerName.isEmpty()) {
                            System.out.println("Player name cannot be empty or null");
                            return;
                        }
                    } catch (Exception ex) {
                        System.out.println("An unexpected error occurred: " + ex.getMessage());
                        return;
                    }

                    System.out.println("Signing up for game: " + game.getName() + ", User: " + userName + ", Player: " + playerName);

                    signUpStage.close();
                });

                signUpLayout.add(labelContainer, 0, 0, 2, 1); // Spanning 2 columns for the label container
                signUpLayout.add(userNameLabel, 0, 1);
                signUpLayout.add(userNameField, 1, 1);
                signUpLayout.add(playerNameLabel, 0, 2);
                signUpLayout.add(playerNameField, 1, 2);
                signUpLayout.add(submitSignupButton, 1, 3);

                Scene signUpScene = new Scene(signUpLayout);
                signUpStage.setScene(signUpScene);
                signUpStage.sizeToScene();
                signUpStage.show();

            });

            //gameInfo.add(signUpButton, 0, 3, 2, 1);

//            ScrollPane gameScrollPane = new ScrollPane();
//            gameScrollPane.setContent(gameInfo);
//            gameScrollPane.setPrefWidth(200);
//            gameScrollPane.setFitToHeight(true);
            VBox buttonBox = new VBox();
            buttonBox.setPadding(new Insets(30, 0, 0, 0));
            buttonBox.getChildren().add(signUpButton);

            HBox gamePane = new HBox();
            gamePane.setPrefWidth(250);
            gamePane.getChildren().addAll(gameInfo, buttonBox);

            gameBox.getChildren().add(gamePane);
            gameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: white;");

            //gameBox.getChildren().addAll(gameScrollPane);
            root.getChildren().add(gameBox);
        }

        // vbox1.getChildren().addAll(title, gameList, addGameBtn, signUpBtn);

        primaryStage.setTitle("RoboRally Game Sign-Up");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }
}
