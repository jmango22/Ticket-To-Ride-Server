package edu.goldenhammer.database;

import edu.goldenhammer.model.Player;

import java.util.List;

/**
 * Created by McKean on 4/17/2017.
 */

public class SQLUserDAO implements IUserDAO{
    @Override
    public Player getPlayerInfo(String player) {
        return null;
    }

    @Override
    public Boolean login(String username, String password) {
        return null;
    }

    @Override
    public List<String> getPlayers(String gameID) {
        return null;
    }

    @Override
    public Boolean createUser(String username, String password) {
        return null;
    }

    @Override
    public void setAccessToken(String username, String accessToken) {

    }
}
