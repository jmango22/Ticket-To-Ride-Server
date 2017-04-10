package edu.goldenhammer.database;

import edu.goldenhammer.model.*;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.commands.EndTurnCommand;

import java.util.List;

public interface IDatabaseController {
/*
Player registration and login
 */

    /**
     * Gets all the authentication information to return when logging in to the game, including username and access token,
     * contained in a Player object
     * @param player the player's username
     * @return a Player object containing the username and access token for the given user.
     */
    Player getPlayerInfo(String player);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    Boolean login(String username, String password);
        List<String> getPlayers(String gameID);
        Boolean createUser(String username, String password);
        void setAccessToken(String userID, String accessToken);

/*
Game management
 */
        Boolean createGame(String name);
        GameList getGames();
        GameList getGames(String player);
        Boolean joinGame(String player, String gameID);
        Boolean leaveGame(String player, String gameID);
        void maybeDropGame(String gameName);

/*
Starting the game
 */
        IGameModel playGame(String gameID);
        boolean allHandsInitialized(String gameName);

/*
Train cards
 */
        TrainCard drawRandomTrainCard(String gameName, String playerName);

    /**
     *
     * @param game_name
     * @param player_name
     * @param slot
     * @return
     */
        TrainCard drawTrainCardFromSlot(String game_name, String player_name, int slot);
        boolean hasDrawnTwoTrainCards(String game_name, String player_name);
        void redealSlotCards(String game_name);
        boolean discardCard(String gameName, String playerName, Color color);
        List<Color> getSlotCardColors(String game_name);

/*
Destination cards
 */
        DestinationCard drawRandomDestinationCard(String gameName, String playerName);
        List<DestinationCard> getPlayerDestinationCards(String game_name, String player_name);
        boolean returnDestCards(String gameName, String playerName, List<DestinationCard> destinationCards);

/*
Messages
 */
        boolean postMessage(String game_name, String player_name, String message);
        List<Message> getMessages(String game_name);

/*
Laying tracks
 */
        boolean canClaimRoute(String game_name, String username, int route_number);
        boolean claimRoute(String game_name, String username, int route_number);
        void removeTrainsFromPlayer(String game_name, String username, int trainsToRemove);
        List<Track> getTracks(String game_name);
        int numTrainsLeft(String game_name, String player_name);

/*
Commands
 */
        boolean addCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll);
        EndTurnCommand getEndTurnCommand(String gameName, int commandNumber, String playerName);
        List<BaseCommand> getCommandsSinceLastCommand(String game_name, String player_name, int lastCommandID);
        boolean validateCommand(BaseCommand command);
        int getNumberOfDrawTrainCommands(String game_name);

/*
End game
 */
        boolean isEndOfGame(String game_name);
        boolean alreadyLastRound(String game_name);

/*
Getting the game model
 */
        GameModel getGameModel(String game_name);
}
