package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;

import java.util.HashMap;
import java.util.List;
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

        // CHECKS
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently signed in");
        }
        
        if (isUserInGame(game, currentUser)) {
            throw new IllegalStateException("You have already joined this game");
        }

        try {
            if (playerName == null || playerName.isEmpty()) { // fallback to user's name
                playerName = currentUser.getName(); 
            }
            
            Map<String, Object> playerData = new HashMap<>(); // player data
            playerData.put("name", playerName);
            playerData.put("user", "/users/" + currentUser.getUid());
            playerData.put("game", "/game/" + game.getUid());

            apiService.getClient().post() // post player data to the server
                    .uri("/player")
                    .body(playerData)
                    .retrieve()
                    .toBodilessEntity();
            
            try {
                Game updatedGame = apiService.getClient().get()
                        .uri("/games/" + game.getUid())
                        .retrieve()
                        .body(Game.class);
                        
                if (updatedGame != null && updatedGame.getPlayers() != null) {
                    boolean userFound = false;             // check if user is in player list
                    for (dk.dtu.compute.course02324.part4.consuming_rest.model.Player player : updatedGame.getPlayers()) {
                        if ((player.getUser() != null && player.getUser().getUid() == currentUser.getUid()) ||
                            (player.getName() != null && player.getName().equals(playerName))) {
                            userFound = true;
                            
                            if (player.getName() == null) { // check for null names; safeguard
                                try {
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("name", playerName);
                                    
                                    apiService.getClient().patch() // patch player data to the server
                                            .uri("/player/" + player.getUid())
                                            .body(updateData)
                                            .retrieve()
                                            .toBodilessEntity();
                                } catch (Exception e) {
                                }
                            }
                            
                            break;
                        }
                    }
                    
                    if (!userFound) {
                        throw new IllegalStateException("Failed to join game: Player not added to game");
                    }
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to join game: " + e.getMessage());
        }
    }
    
    private boolean isUserInGame(Game game, User user) {
        if (game == null || user == null || game.getPlayers() == null) { return false; } // null checks
        
        if (user.getName() != null && user.getName().equals(game.getOwner())) { return true; } // is owner
        
        if (game.getPlayers() != null) {
            for (dk.dtu.compute.course02324.part4.consuming_rest.model.Player player : game.getPlayers()) {
                if (player.getUser() != null && player.getUser().getUid() == user.getUid()) { return true; } // check by player UID first
                if (player.getUser() != null && player.getUser().getUid() == user.getUid()) { return true; } // check by name
                
                if (player.getName() != null && player.getName().equals(user.getName())) { return true; } // check by name
                
                if (player.getName() == null && player.getUser() != null && player.getUser().getUid() == user.getUid()) { return true; } // check by name
            }
        }
        
        return false;
    }

    public boolean isPlayerInGame(Game game, User user) {
        if (game == null || user == null) { return false; } // null checks for game and user
        
        List<Player> players = game.getPlayers();
        if (players == null) { return false; } // null checks for players
        
        boolean isInGame = false;
        
        for (Player player : players) {
            // yes it's ugly but the one-liners save space 
            if (player.getUser() != null && player.getUser().getUid() == user.getUid()) { isInGame = true; break; } // check by User ID first
            if (player.getUser() != null && player.getUser().getUid() == user.getUid()) { isInGame = true; break; } // check by name
            if (player.getName() != null && player.getName().equals(user.getName())) { isInGame = true; break; } // check by name
            if (player.getName() == null && player.getUser() != null && player.getUser().getUid() == user.getUid()) { isInGame = true; break; } // check by name

            
            if (player.getUser() == null &&  // check for if player has no user reference
                player.getName() != null && 
                player.getName().equals(user.getName())) {
                isInGame = true;
                break;
            }
        }
        
        // finally: if not in player list, check if the user is the host
        if (!isInGame && user.getName() != null && user.getName().equals(game.getOwner())) {
            return true;
        }
        
        return isInGame;
    }
    
    // while it may look messy, it's actually quite a robust safeguard
    public String getDisplayablePlayerName(Player player) {
        String playerName = player.getName();
        
        // fix for null player names: Try to extract from user object if available
        if ((playerName == null || playerName.equals("null")) && player.getUser() != null) {
            playerName = player.getUser().getName();
        }
        
        // if we still have a null name but have a player ID, use a placeholder
        if ((playerName == null || playerName.equals("null")) && player.getUid() > 0) {
            playerName = "Player #" + player.getUid();
        }
        
        return playerName;
    }

    public String getCurrentUsername() {
        User currentUser = userController.getCurrentUser();
        return currentUser != null ? currentUser.getName() : "";
    }
}