package edu.goldenhammer.model;

import java.util.List;

/**
 * Created by seanjib on 2/11/2017.
 */
public class GameModel implements IGameModel {
    private List<PlayerOverview> players;
    private List<DestinationCard> destinationDeck;
    private List<TrainCard> trainCardDeck;
    private Map map;
    private GameName name;
    private List<Color> mBank;
    private int checkpointIndex;

    public GameModel(List<PlayerOverview> players, List<DestinationCard> destinationDeck, List<TrainCard> trainCardDeck, Map map, GameName name, List<Color> mBank) {
        this.players = players;
        this.destinationDeck = destinationDeck;
        this.trainCardDeck = trainCardDeck;
        this.map = map;
        this.name = name;
        this.mBank = mBank;
        checkpointIndex = 0;
    }

    public int getCheckpointIndex() {
        return checkpointIndex;
    }

    public void setCheckpointIndex(int checkpointIndex) {
        this.checkpointIndex = checkpointIndex;
    }

    public List<PlayerOverview> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerOverview> players) {
        this.players = players;
    }

    public List<DestinationCard> getDestinationDeck() {
        return destinationDeck;
    }

    public void setDestinationDeck(List<DestinationCard> destinationDeck) {
        this.destinationDeck = destinationDeck;
    }

    public List<TrainCard> getTrainCardDeck() {
        return trainCardDeck;
    }

    public void setTrainCardDeck(List<TrainCard> trainCardDeck) {
        this.trainCardDeck = trainCardDeck;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public GameName getName() {
        return name;
    }

    public void setName(GameName name) {
        this.name = name;
    }
}