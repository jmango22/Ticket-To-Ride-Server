package edu.goldenhammer.model;

import edu.goldenhammer.data_types.Game;
import edu.goldenhammer.data_types.GameList;
import edu.goldenhammer.data_types.Player;

import java.util.List;

public interface IDatabaseController {
    Player getPlayerInfo(String player);
    GameList getGames();
    GameList getGames(String player);
    Boolean login(String username, String password);
    Boolean createUser(String username, String password);
    Boolean createGame(String name);
    Boolean joinGame(String player, String gameID);
    List<Player> getPlayers(String gameID);
}
