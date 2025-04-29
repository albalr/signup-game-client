package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;

import java.util.List;

public class GameController {
    private final RestApiService apiService;

    public GameController(RestApiService apiService) {
        this.apiService = apiService;
    }

    public List<Game> getAllGames() {
        return apiService.getClient().get()
                .uri("/game")
                .retrieve()
                .body(HALWrapperGames.class)
                .getGames();
    }

    public Game createGame(String name, int minPlayers, int maxPlayers) {
        Game game = new Game();
        game.setName(name);
        game.setMinPlayers(minPlayers);
        game.setMaxPlayers(maxPlayers);

        return apiService.getClient().post()
                .uri("/game")
                .body(game)
                .retrieve()
                .body(Game.class);
    }

    public void deleteGame(Game game) {
        apiService.getClient().delete()
                .uri("/game/" + game.getUid())
                .retrieve()
                .body(Void.class);
    }
}