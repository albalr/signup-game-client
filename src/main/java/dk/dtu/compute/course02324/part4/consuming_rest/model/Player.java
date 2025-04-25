package dk.dtu.compute.course02324.part4.consuming_rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    private int uid;
    private String name;
    private Object user;
    private Object game;

    // Default no-argument constructor for Jackson
    public Player() {
    }

    // Constructor for creating new players
    public Player(String name, Object user, Object game) {
        this.name = name;
        this.user = user;
        this.game = game;
    }

    // Special constructor for handling numeric IDs - this is what fixes the error
    @JsonCreator
    public static Player fromId(int id) {
        Player player = new Player();
        player.uid = id;
        return player;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public Object getGame() {
        return game;
    }

    public void setGame(Object game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Player{uid=" + uid + ", name='" + name + "'}";
    }
}
