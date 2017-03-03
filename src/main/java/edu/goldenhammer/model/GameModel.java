package edu.goldenhammer.model;

import java.util.List;
import java.util.Map;

/**
 * Created by seanjib on 2/11/2017.
 */
public class GameModel implements IGameModel {
    public GameModel(String id, String name, boolean started, List<String> players){
        this.id = id;
        this.name = name;
        this.started = started;
        this.players = players;
    }


    public String getID() {
        return id;
    }


    public String getName() {
        return name;
    }

    public boolean isStarted() {
        return started;
    }

    public List<String> getPlayers() {
        return players;
    }

    @Override
    public void setPlayers(List<String> players) { this.players = players; }

    private String id;
    private String name;
    private boolean started;
    private List<String> players;
    private Map<String, Integer> otherPlayerTrainCards;
    private Map<String, Integer> otherPlayerDestinationCards;
    private Map<String, Integer> trainsLeft;
    private int playerTurn;
    private List<String> messages; //TODO: create the Message model class
    private List<String> faceUpTrainCards; //TODO: create the TrainCard model class
    private List<String> playerTrainCards;
    private List<String> playerDestinationCards; //TODO: create the DestinationCard model class
    private List<String> claimedRoutes; //TODO: create the ClaimedRoute model class



}
