package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameController {
    private final RestApiService apiService; // i.e. RestApiService which does the actual api calls
    private User currentUser;

    public GameController(RestApiService apiService, User currentUser) { // constructor
        this.apiService = apiService;
        this.currentUser = currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Game> getAllGames() {
        try {
            ResponseEntity<List<Game>> response = apiService.getClient().get()
                    .uri("/games/allgames")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<Game>>() {});
            
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (RestClientException e) {
            List<Game> openGames = getOpenGames(); // opengames as fallback
            return openGames != null ? openGames : new ArrayList<>();
        }
    }

    public List<Game> getOpenGames() {
        try {
            ResponseEntity<List<Game>> response = apiService.getClient().get()
                    .uri("/games/opengames")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<Game>>() {});
            
            List<Game> games = response.getBody();
            return games != null ? games : new ArrayList<>();
        } catch (RestClientException e) {
            return new ArrayList<>(); // pretty poor fallback but it's the best we can do
        }
    }

    public Game createGame(String name, int minPlayers, int maxPlayers) {
        validateCurrentUser();

        Game game = new Game();
        game.setName(name);             // set info     
        game.setMinPlayers(minPlayers);
        game.setMaxPlayers(maxPlayers);
        game.setOwner(currentUser.getName());
        game.setStatus(Game.GameStatus.SIGNUP);

        try {
            Game createdGame = apiService.getClient().post()
                    .uri("/games/create")
                    .body(game)
                    .retrieve()
                    .body(Game.class);
            
            if (createdGame == null) {
                throw new IllegalStateException("Failed to create game: server returned null");
            }
            
//            apiService.getClient().post()
//                    .uri("/games/" + createdGame.getUid() + "/join?username=" + currentUser.getName())
//                    .retrieve()
//                    .toBodilessEntity();
            
            return getGame(createdGame.getUid()); // Get fresh game state
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to create game: " + e.getMessage());
        }
    }

    public Game getGame(long gameId) {
        try {
            return apiService.getClient().get()
                    .uri("/games/" + gameId)
                    .retrieve()
                    .body(Game.class);
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to fetch game: " + e.getMessage());
        }
    }

    public Game leaveGame(Game game) {
        // this method is a bit more messy than the others, but the error handling is important.

        validateCurrentUser();
        boolean isPlayerInGame = false; // i.e. if player is in game/lobby
        
        if (game.getPlayers() != null) { // i.e. if game has players
            isPlayerInGame = game.getPlayers().stream() 
                .anyMatch(player -> 
                    (player.getUser() != null && player.getUser().getUid() == currentUser.getUid()) ||
                    (player.getName() != null && player.getName().equals(currentUser.getName()))
                );
        }
        
        if (!isPlayerInGame) { // i've rarely seen this error, but it's good to have
            throw new IllegalStateException("You are not a player in this game");
        }

        try {
            apiService.getClient().post()
                    .uri("/games/" + game.getUid() + "/leave?username=" + currentUser.getName())
                    .retrieve()
                    .toBodilessEntity();
            
            return getGame(game.getUid());  // return updated game state you know the drill
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to leave game: " + e.getMessage());
        }
    }

    public void startGame(Game game) {
        validateCurrentUser();
        
        if (!Objects.equals(currentUser.getName(), game.getOwner())) { // owner check
            throw new IllegalStateException("Only the game owner can start the game");
        }

        try {
            apiService.getClient().post()
                    .uri("/games/" + game.getUid() + "/start?username=" + currentUser.getName())
                    .retrieve()
                    .toBodilessEntity();
                    
            // short delay for server to process agains
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // get fresh game state
            Game updatedGame = getGame(game.getUid());
            if (updatedGame == null || updatedGame.getStatus() != Game.GameStatus.ACTIVE) {
                throw new IllegalStateException("Failed to start game: Game status not updated");
            }
        } catch (RestClientException e) {
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("Not enough players")) {
                throw new IllegalStateException("You cannot start the game until there is at least one other player in the lobby!");
            }
            
            throw new IllegalStateException("Failed to start game: " + errorMessage);
        }
    }

    public void deleteGame(Game game) {
        validateCurrentUser();
        
        if (!Objects.equals(currentUser.getName(), game.getOwner())) { // must be owner
            throw new IllegalStateException("Only the game owner can delete the game");
        }

        try {
            if (game.getPlayers() != null) {  // removes players first
                for (Player player : game.getPlayers()) {
                    String playerName = player.getName();
                    if (playerName == null && player.getUser() != null) {
                        playerName = player.getUser().getName();
                    }
                    
                    if (playerName != null && !playerName.isEmpty()) {
                        try {
                            apiService.getClient().post()
                                .uri("/games/" + game.getUid() + "/leave?username=" + playerName)
                                .retrieve()
                                .toBodilessEntity();
                        } catch (RestClientException ignored) {
                        }
                    }
                }
            }

            apiService.getClient().delete() // then delete game
                    .uri("/games/" + game.getUid() + "?username=" + currentUser.getName())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to delete game: " + e.getMessage());
        }
    }

    public boolean isUserHost(Game game, User user) {
        if (game == null || user == null || user.getName() == null) {
            return false;
        }
        
        return user.getName().equals(game.getOwner());
    }
    
    public boolean isCurrentUserHost(Game game) {
        if (game == null || currentUser == null || currentUser.getName() == null) {
            return false;
        }
        
        return currentUser.getName().equals(game.getOwner());
    }
    
    public int getPlayerCount(Game game, User user) {
        if (game == null || game.getPlayers() == null) {
            return 0;
        }
        
        long validPlayerCount = game.getPlayers().stream() // count all valid players in the list
            .filter(player -> 
                (player.getName() != null && !player.getName().isEmpty() && !player.getName().equals("null")) ||
                (player.getUser() != null && player.getUser().getName() != null && !player.getUser().getName().isEmpty())
            )
            .count();
            
        boolean hostInList = game.getPlayers().stream() // check if host is in the player list
            .anyMatch(player -> 
                (player.getName() != null && player.getName().equals(game.getOwner())) ||
                (player.getUser() != null && player.getUser().getName() != null && 
                 player.getUser().getName().equals(game.getOwner()))
            );
            
        if (!hostInList && game.getOwner() != null && !game.getOwner().isEmpty()) { // if host is not in the list, add them to the count
            validPlayerCount++;
        }
            
        return (int) validPlayerCount;
    }
    
    public boolean canStartGame(Game game) {
        if (currentUser == null) return false;                                      // if user logged in
        if (!Objects.equals(currentUser.getName(), game.getOwner())) return false;  // is owner
        if (game.getStatus() != Game.GameStatus.SIGNUP) return false;               // signup phase
        if (game.getPlayers() == null) return false;                                // has players
        
        int playerCount = getPlayerCount(game, currentUser);
        if (playerCount < game.getMinPlayers()) return false;
        
        return true;
    }
    
    // super important. Duplicate names mess with the DB
    public boolean doesGameNameExist(String name) { 
        try {                                       
            ResponseEntity<List<Game>> response = apiService.getClient().get()
                    .uri("/games/searchgames?name=" + name)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<Game>>() {});
            
            List<Game> games = response.getBody();
            return games != null && !games.isEmpty();
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to check game name: " + e.getMessage());
        }
    }
    
    private void validateCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently signed in");
        }
    }
}