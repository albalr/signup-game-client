package dk.dtu.compute.course02324.part4.consuming_rest.wrappers;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Players {

    private List<Player> player;

    public List<Player> getPlayer() {
        return player;
    }

    public void setPlayer(List<Player> player) {
        this.player = player;
    }

}
