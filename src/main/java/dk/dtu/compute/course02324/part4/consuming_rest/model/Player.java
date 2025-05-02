package dk.dtu.compute.course02324.part4.consuming_rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
        scope = Player.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "uid")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    private int uid;
    private String name;
    private User user;
    private Game game;

    // Default no-argument constructor for Jackson
    public Player() {
    }

    public Player(String name, Object user, Object game) { // constructor
        this.name = name;
        this.user = user instanceof User ? (User) user : null;
        this.game = game instanceof Game ? (Game) game : null;
    }

    @JsonCreator
    public static Player fromId(int id) { // special constructor for numeric IDs;
        Player player = new Player();     // necessary for error
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Player{uid=" + uid + ", name='" + name + "'}";
    }
}
