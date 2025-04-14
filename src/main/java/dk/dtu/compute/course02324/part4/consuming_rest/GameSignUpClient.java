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

    // see
    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-resttemplate

    public static void main(String[] args) {

        launch(args);

        // TODO try to read out the available games from the backend, show them on a
        //  simple graphical GUI and sign up for a game using some of the operations
        //  at the top. -- done
        //  For the GUI to work in JavaFX, you need to add some maven dependencies
        //  (see pom file for Assignment 3). -- done

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Set up the REST client for connecting to the backend
        RestClient customClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();

        // Get the list of games from the backend
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
            // System.out.println("Create Game button clicked");

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
                String gameName = nameField.getText();
                // catch errors here fx if gameName is empty or null
                int minPlayers = Integer.parseInt(minPlayersField.getText());
                // catch errors here fx if minPlayers is empty, null or not a number
                int maxPlayers = Integer.parseInt(maxPlayersField.getText());
                // catch errors here fx if maxPlayers is empty, null or not a number

                //System.out.println("Game Created: " + gameName + ", Min Players: " + minPlayers + ", Max Players: " + maxPlayers);

                // TODO: Implement the game creation logic here -- done
                try {
                    Game newGame = new Game();
                    newGame.setName(gameName);
                    newGame.setMinPlayers(minPlayers);
                    newGame.setMaxPlayers(maxPlayers);

                    customClient.post()
                            .uri("/game")
                            .body(newGame)
                            .retrieve()
                            .body(Game.class);

                    //System.out.println("Game Successfully Created");

                } catch (Exception ex) {
                    //System.out.println("Failed to create game: " + ex.getMessage());
                    ex.printStackTrace();
                }

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

        // Loop through all games and create UI for each
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
                    String userName = userNameField.getText();
                    // catch errors here fx if userName is empty or null
                    String playerName = playerNameField.getText();
                    // catch errors here fx if playerName is empty, null or not a number

                    //System.out.println("Signing up for game: " + game.getName() + ", User: " + userName + ", Player: " + playerName);

                    // TODO: Implement the sign-up logic here -- done
                    try {
                        // check if user exists
                        List<User> users = customClient.get()
                                .uri("/user")
                                .retrieve()
                                .body(HALWrapperUsers.class)
                                .getUsers();

                        User user = users.stream()
                                .filter(u -> userName.equalsIgnoreCase(u.getName()))
                                .findFirst()
                                .orElse(null);

                        // if the user does not exist, create a new one
                        if (user == null) {
                            User newUser = new User();
                            newUser.setName(userName);

                            customClient.post()
                                    .uri("/user")
                                    .body(newUser)
                                    .retrieve()
                                    .toBodilessEntity(); // don't expect a return body

                            // fetch again to obtain UID
                            user = customClient.get()
                                    .uri("/user")
                                    .retrieve()
                                    .body(HALWrapperUsers.class)
                                    .getUsers()
                                    .stream()
                                    .filter(u -> userName.equalsIgnoreCase(u.getName()))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Failed to create and retrieve user"));
                        }

                        // create a new Player linked to the user and game
                        Map<String, Object> playerData = new HashMap<>();
                        playerData.put("name", playerName);
                        playerData.put("user", "/user/" + user.getUid());
                        playerData.put("game", "/game/" + game.getUid());

                        Player createdPlayer = customClient.post()
                                .uri("/player")
                                .body(playerData)
                                .retrieve()
                                .body(Player.class);

                        //System.out.println("Player signed up: " + createdPlayer.getName());

                    } catch (Exception ex) {
                        System.err.println("Error during sign-up: " + ex.getMessage());
                        ex.printStackTrace();
                    }

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
