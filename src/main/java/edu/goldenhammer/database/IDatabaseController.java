package edu.goldenhammer.database;

import edu.goldenhammer.database.data_types.IServerGame;
import edu.goldenhammer.model.GameList;
import edu.goldenhammer.database.data_types.IServerPlayer;

import java.util.List;

public interface IDatabaseController {
    IServerPlayer getPlayerInfo(String player);
    GameList getGames();
    GameList getGames(String player);
    Boolean login(String username, String password);
    Boolean createUser(String username, String password);
    Boolean createGame(String name);
    Boolean joinGame(String player, String gameID);
    List<IServerPlayer> getPlayers(String gameID);
    Boolean leaveGame(String player, String gameID);
    IServerGame playGame(String player, String gameID);
    void setAccessToken(String userID, String accessToken);
}
