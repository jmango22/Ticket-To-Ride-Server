package edu.goldenhammer.database;

import edu.goldenhammer.database.data_types.*;
import edu.goldenhammer.model.*;
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
    void setAccessToken(String userID, String accessToken);
    void maybeDropGame(String gameName);
    DatabaseTrainCard drawRandomTrainCard(String gameName, String playerName);
    DatabaseTrainCard drawTrainCardFromSlot(String game_name, String player_name, int slot);
    DatabaseDestinationCard drawRandomDestinationCard(String gameName, String playerName);
    DatabaseCity getCity(int cityID);
    boolean hasDestinationCards(String gameName, String playerName);
    boolean addCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll);
    boolean returnDestCards(String gameName, String playerName, List<DestinationCard> destinationCards);
    boolean postMessage(String game_name, String player_name, String message);
    List<DatabaseMessage> getMessages(String game_name);
    String getUsername(String player_id);
    boolean allHandsInitialized(String gameName);
    int getNumberOfDrawTrainCommands(String game_name);
    List<DatabaseDestinationCard> drawDestinationCards(String game_name, String username, int commandNumber, int player_number);
    boolean discardCard(String gameName, String playerName, Color color);
    List<DatabaseTrainCard> getSlotCards(String game_name);
    boolean validateCommand(BaseCommand command);
    boolean hasDrawnTwoTrainCards(String game_name, String player_name);
    int numTrainsLeft(String game_name, String player_name);
    boolean isEndOfGame(String game_name);
    DatabaseTrainCard getTrainCardFromSlot(String game_name, int slot);
    void redealSlotCards(String game_name);
}
