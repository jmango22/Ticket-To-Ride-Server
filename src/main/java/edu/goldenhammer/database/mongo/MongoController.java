package edu.goldenhammer.database.mongo;

import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.model.*;
import edu.goldenhammer.mongoStuff.MongoDriver;
import edu.goldenhammer.mongoStuff.MongoGame;
import edu.goldenhammer.mongoStuff.MongoUser;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.commands.EndTurnCommand;
import edu.goldenhammer.server.commands.LastTurnCommand;

import java.net.UnknownHostException;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by seanjib on 4/9/2017.
 */
public class MongoController implements IDatabaseController{
    private MongoDriver driver;
    private TreeMap gameModels;

    public MongoController(){
        driver = new MongoDriver();
        gameModels = new TreeMap<String, GameModel>();
    }

    @Override
    public Player getPlayerInfo(String player) {
        return null;
    }

    @Override
    public Boolean login(String username, String password) {
        MongoUser user;
        try{
            user = driver.getUser(username);
            return user!=null && user.getPassword().equals(password);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> getPlayers(String gameID) {
        return null;
    }

    @Override
    public Boolean createUser(String username, String password) {
        MongoUser u = new MongoUser(username,password);
        try {
            if (driver.getUser(username) != null){
                return false;
            }
            else{
                driver.setUser(u);
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setAccessToken(String username, String accessToken) {
        try{
            MongoUser u = driver.getUser(username);
            u.setToken(accessToken);
            driver.setUser(u);
        }catch(Exception e){

        }
    }

    @Override
    public Boolean createGame(String name) {
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
    public Boolean joinGame(String player, String gameID) {
        return null;
    }

    @Override
    public Boolean leaveGame(String player, String gameID) {
        return null;
    }

    @Override
    public void maybeDropGame(String gameName) {

    }

    @Override
    public IGameModel playGame(String gameID) {
        return null;
    }

    @Override
    public boolean allHandsInitialized(String gameName) {
        return false;
    }

    @Override
    public TrainCard drawRandomTrainCard(String gameName, String playerName) {
        return null;
    }

    @Override
    public TrainCard drawTrainCardFromSlot(String game_name, String player_name, int slot) {
        return null;
    }

    @Override
    public boolean hasDrawnTwoTrainCards(String game_name, String player_name) {
        return false;
    }

    @Override
    public void redealSlotCards(String game_name) {

    }

    @Override
    public boolean discardCard(String gameName, String playerName, Color color) {
        return false;
    }

    @Override
    public List<Color> getSlotCardColors(String game_name) {
        return null;
    }

    @Override
    public DestinationCard drawRandomDestinationCard(String gameName, String playerName) {
        return null;
    }

    @Override
    public List<DestinationCard> getPlayerDestinationCards(String game_name, String player_name) {
        return null;
    }

    @Override
    public boolean returnDestCards(String gameName, String playerName, List<DestinationCard> destinationCards) {
        return false;
    }

    @Override
    public boolean postMessage(String game_name, String player_name, String message) {
        return false;
    }

    @Override
    public List<Message> getMessages(String game_name) {
        return null;
    }

    @Override
    public boolean canClaimRoute(String game_name, String username, int route_number) {
        return false;
    }

    @Override
    public boolean claimRoute(String game_name, String username, int route_number) {
        return false;
    }

    @Override
    public void removeTrainsFromPlayer(String game_name, String username, int trainsToRemove) {

    }

    @Override
    public List<Track> getTracks(String game_name) {
        return null;
    }

    @Override
    public int numTrainsLeft(String game_name, String player_name) {
        return 0;
    }

    @Override
    public boolean addCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll) {
        return false;
    }

    @Override
    public EndTurnCommand getEndTurnCommand(String gameName, int commandNumber, String playerName) {
        return null;
    }

    @Override
    public List<BaseCommand> getCommandsSinceLastCommand(String game_name, String player_name, int lastCommandID) {
        return null;
    }

    @Override
    public boolean validateCommand(BaseCommand command) {
        return false;
    }

    @Override
    public int getNumberOfDrawTrainCommands(String game_name) {
        return 0;
    }

    @Override
    public boolean isEndOfGame(String game_name) {
        try {
            int player = -1;
            boolean lastRound = false;

            MongoGame currentGame = driver.getGame(game_name);
            for (BaseCommand command : currentGame.getCommands()) {
                if (lastRound && (command.getName().equals("EndTurn") && command.getPlayerNumber() == player)) {
                    return true;
                } else if (command.getName().equals("LastTurn")) {
                    player = command.getPlayerNumber();
                    lastRound = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean alreadyLastRound(String game_name) {
        try {
            MongoGame currentGame = driver.getGame(game_name);
            for (BaseCommand command : currentGame.getCommands()) {
                if (command.getName().equals("LastTurn")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public GameModel getGameModel(String game_name) {
        GameModel game;
        if(gameModels.containsKey(game_name)) {
            game = (GameModel) gameModels.get(game_name);
        } else {
            try {
                game = driver.getGame(game_name).getCheckpoint();
                if(game != null) {
                    gameModels.put(game_name, game);
                }
            } catch (UnknownHostException uh) {
                uh.printStackTrace();
                game = null;
            }
        }
        return game;
    }
}
