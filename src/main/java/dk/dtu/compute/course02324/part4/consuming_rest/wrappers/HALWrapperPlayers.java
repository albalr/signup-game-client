package dk.dtu.compute.course02324.part4.consuming_rest.wrappers;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HALWrapperPlayers {

    private Players _embedded;

    public Players get_embedded() {
        return _embedded;
    }

    public void set_embedded(Players _embedded) {
        this._embedded = _embedded;
    }

    public List<Player> getPlayers() {
        if (_embedded != null && _embedded.getPlayer() != null) {
            return _embedded.getPlayer();
        }

        return null;
    }


}
