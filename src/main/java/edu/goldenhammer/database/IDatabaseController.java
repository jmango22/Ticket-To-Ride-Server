package edu.goldenhammer.database;

import edu.goldenhammer.model.GameList;
import edu.goldenhammer.database.data_types.IDatabasePlayer;
import edu.goldenhammer.model.IGameModel;

import java.util.List;

public interface IDatabaseController {
    IDatabasePlayer getPlayerInfo(String player);
    GameList getGames();
    GameList getGames(String player);
    Boolean login(String username, String password);
    Boolean createUser(String username, String password);
    Boolean createGame(String name);
    Boolean joinGame(String player, String gameID);
    List<String> getPlayers(String gameID);
    Boolean leaveGame(String player, String gameID);
    IGameModel playGame(String gameID);
    void setAccessToken(String userID, String accessToken);
    void maybeDropGame(String gameName);
}
