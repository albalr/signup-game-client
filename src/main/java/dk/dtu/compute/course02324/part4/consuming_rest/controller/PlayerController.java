package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;

import java.util.HashMap;
import java.util.Map;

public class PlayerController {
    private final RestApiService apiService;
    private final UserController userController;

    public PlayerController(RestApiService apiService, UserController userController) {
        this.apiService = apiService;
        this.userController = userController;
    }

    public void signUpForGame(Game game, String playerName) {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently signed in");
        }

        Map<String, Object> playerData = new HashMap<>();
        playerData.put("name", playerName);
        playerData.put("user", "/users/" + currentUser.getUid());
        playerData.put("game", "/game/" + game.getUid());

        apiService.getClient().post()
                .uri("/player")
                .body(playerData)
                .retrieve()
                .toBodilessEntity();
    }
}