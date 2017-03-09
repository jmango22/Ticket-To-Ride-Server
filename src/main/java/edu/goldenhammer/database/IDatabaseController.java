package edu.goldenhammer.database;

import edu.goldenhammer.database.data_types.DatabaseCity;
import edu.goldenhammer.database.data_types.DatabaseDestinationCard;
import edu.goldenhammer.database.data_types.DatabaseTrainCard;
import edu.goldenhammer.model.DestinationCard;
import edu.goldenhammer.model.GameList;
import edu.goldenhammer.database.data_types.IDatabasePlayer;
import edu.goldenhammer.model.IGameModel;
import edu.goldenhammer.model.TrainCard;
import edu.goldenhammer.server.commands.BaseCommand;

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
    IGameModel getGameModel(String game_name);
    int getDestinationCardCount(String player_name);
    int getDestinationCardCount(int player_id);
    List<TrainCard> getTrainCards(String game_name);
    void setAccessToken(String userID, String accessToken);
    void maybeDropGame(String gameName);
    DatabaseTrainCard drawRandomTrainCard(String gameName, String playerName);
    DatabaseDestinationCard drawRandomDestinationCard(String gameName, String playerName);
    List<BaseCommand> getCommandsSinceLastCommand(String game_name, String player_name, int lastCommandID);
    boolean claimRoute(String game_name, String username, int route_number);
    DatabaseCity getCity(int cityID);
    boolean hasDestinationCards(String gameName, String playerName);
    void addCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll);
    boolean returnDestCards(String gameName, String playerName, List<DestinationCard> destinationCards);
}
