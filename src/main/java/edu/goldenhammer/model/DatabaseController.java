package edu.goldenhammer.model;

import edu.goldenhammer.data_types.GameList;
import edu.goldenhammer.data_types.Player;

import java.util.List;

/**
 * Created by devonkinghorn on 2/4/17.
 */
public class DatabaseController implements IDatabaseController {

    public DatabaseController(){

    }
    public void initializeDatabase() {

    }
    @Override
    public Player getPlayerInfo(String player) {
        return null;
    }

    @Override
    public GameList getGames() {
        return null;
    }

    @Override
    public GameList getGames(String player) {
        return null;
    }

    @Override
    public Boolean login(String username, String password) {
        return null;
    }

    @Override
    public Boolean createUser(String username, String password) {
        return null;
    }

    @Override
    public Boolean createGame(String name) {
        return null;
    }

    @Override
    public Boolean joinGame(String player, String gameID) {
        return null;
    }

    @Override
    public List<Player> getPlayers(String gameID) {
        return null;
    }
}
