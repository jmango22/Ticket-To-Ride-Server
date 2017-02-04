package edu.goldenhammer.data_types;

import java.io.Serializable;
import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public class ServerGameListItem implements GameListItem, Serializable {

    public ServerGameListItem(String id, String name, boolean started, List<String> players) {
        this.id = id;
        this.name = name;
        this.started = started;
        this.players = players;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public List<String> getPlayers() {
        return players;
    }

    private String id;
    private String name;
    private boolean started;
    private List<String> players;
}
