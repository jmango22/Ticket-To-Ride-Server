package edu.goldenhammer.model;

import edu.goldenhammer.data_types.IGameListItem;

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

    public List<IGame> getGameList() {
        return gameList;
    }

    public void add(IGame g) {
        gameList.add(g);
    }

    private List<IGame> gameList;
}
