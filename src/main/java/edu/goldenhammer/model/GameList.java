package edu.goldenhammer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public class GameList implements Serializable {
    public GameList() {
        gameList = new ArrayList<>();
    }

    public List<IGameOverview> getGameList() {
        return gameList;
    }

    public void add(IGameOverview g) {
        gameList.add(g);
    }

    private List<IGameOverview> gameList;
}
