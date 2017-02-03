package edu.goldenhammer.data_types;

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

    public List<GameListItem> getGameList() {
        return gameList;
    }

    public void add(GameListItem g) {
        gameList.add(g);
    }

    private List<GameListItem> gameList;
}
