package edu.goldenhammer.data_types;

import java.io.Serializable;
import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public class ServerGame implements Game, Serializable {
    public ServerGame(String id, String name, List<String> players){
        this.id = id;
        this.name = name;
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
    public List<String> getPlayers() {
        return players;
    }

    private String id;
    private String name;
    private List<String> players;

}
