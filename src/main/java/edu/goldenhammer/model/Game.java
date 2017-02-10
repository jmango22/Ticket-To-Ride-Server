package edu.goldenhammer.model;

import java.util.List;

/**
 * Created by devonkinghorn on 2/8/17.
 */
public class Game implements IGame {
    public Game(String id, String name, List<String> players){
        this.id = id;
        this.name = name;
        this.players = players;
    }


    public String getID() {
        return id;
    }


    public String getName() {
        return name;
    }


    public List<String> getPlayers() {
        return players;
    }

    private String id;
    private String name;
    private List<String> players;

}
