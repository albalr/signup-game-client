package dk.dtu.compute.course02324.part4.consuming_rest.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;
import java.util.ArrayList;

@JsonIdentityInfo(
        scope = Game.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "uid")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    public enum GameStatus {
        INITIAL,
        SIGNUP,
        ACTIVE,
        FINISHED
    }

    private long uid;

    private String name;

    private int minPlayers;

    private int maxPlayers;

    // TODO There could be more attributes here, like
    //      in which state is the sign up for the game, did
    //      the game started or finish (after the game started
    //      you might not want new players coming in etc.)
    //      See analogous classes in backend.

    private List<Player> players = new ArrayList<>();

    private String owner;

    private GameStatus status;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        if (players == null) {
            players = new ArrayList<>();
        }
        return players;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setPlayers(List<Player> players) {
        this.players = players != null ? players : new ArrayList<>();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Game{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", status=" + status +
                // ", players=" + players +
                '}';
    }
}
