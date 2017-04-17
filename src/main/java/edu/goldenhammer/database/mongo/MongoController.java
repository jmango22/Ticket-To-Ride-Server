package edu.goldenhammer.database.mongo;

import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.model.*;
import edu.goldenhammer.mongoStuff.MongoDriver;
import edu.goldenhammer.mongoStuff.MongoGame;
import edu.goldenhammer.mongoStuff.MongoUser;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.commands.EndTurnCommand;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by seanjib on 4/9/2017.
 */
public class MongoController implements IDatabaseController{
    private int MAX_TRAIN;
    private MongoDriver driver;
    private int betweenCheckpoint;

    private TreeMap mongoGames;

    public MongoController(int maxTrain, int betweenCheckpoint) {
        MAX_TRAIN=maxTrain;
        driver = new MongoDriver();
        mongoGames = new TreeMap<String, GameModel>();
        this.betweenCheckpoint = betweenCheckpoint;
    }

    public MongoController(){
        MAX_TRAIN=45;
        driver = new MongoDriver();
        mongoGames = new TreeMap<String, GameModel>();
        betweenCheckpoint = 5;
    }

    private MongoGame getGame(String game_name) {
        MongoGame game;
        if(mongoGames.containsKey(game_name)) {
            game = (MongoGame) mongoGames.get(game_name);
        } else {
            try {
                game = driver.getGame(game_name);
                if(game != null) {
                    mongoGames.put(game_name, game);
                }
            } catch (UnknownHostException uh) {
                uh.printStackTrace();
                game = null;
            }
        }
        return game;
    }

    private int getPlayerNumber(MongoGame currentGame, String player_name) {
        int playerId = -1;
        for(PlayerOverview player : currentGame.getCheckpoint().getPlayers()) {
            if(player.getUsername().equals(player_name)) {
                playerId = player.getPlayer();
            }
        }
        return playerId;
    }

    @Override
    public Player getPlayerInfo(String player) {
        try{
            MongoUser user = driver.getUser(player);
            if (user == null){
                return null;
            }
            else{
                return new Player(player,user.getToken());
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
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
        try{
            MongoGame mg = driver.getGame(gameID);
            if (mg == null){
                return null;
            }
            else{
                return mg.getPlayers();
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
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
        try{
            MongoGame g = driver.getGame(name);
            if (g == null){
                MongoGame creation = new MongoGame(name);
                driver.setGame(creation);
                return true;
            }
            else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public GameList getGames() {
        try{
            List<MongoGame> list = driver.getAllGames();
            GameList gameList = new GameList();
            for (MongoGame mg : list){
                Boolean started = mg.getCheckpoint() != null;
                gameList.add(new GameListItem(mg.getGameName(),mg.getGameName(), started,mg.getPlayers()));
            }
            return gameList;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GameList getGames(String player) {
       try{
           List<MongoGame> games = driver.getGamesWithPlayer(player);
           GameList gameList = new GameList();
           for (MongoGame mg : games){
               Boolean started = mg.getCheckpoint() == null;
               GameListItem gli = new GameListItem(mg.getGameName(),mg.getGameName(),started,mg.getPlayers());
               gameList.add(gli);
           }
           return gameList;
       }catch(Exception e){
           e.printStackTrace();
           return null;
       }
    }

    @Override
    public Boolean joinGame(String player, String gameName) {
        try{
            MongoGame mg = driver.getGame(gameName);
            if (mg == null || mg.getPlayers().size() >= 5 || mg.getCheckpoint() != null){
                return false;
            }
            else{
                List<String> players = mg.getPlayers();
                players.add(player);
                mg.setPlayers(players);
                driver.setGame(mg);
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean leaveGame(String player, String gameName) {
        try{
            MongoGame mg = driver.getGame(gameName);
            if (mg == null || mg.getCheckpoint() != null){
                return false;
            }
            else{
                List<String> players = mg.getPlayers();
                if (!players.contains(player)){
                    return false;
                }
                else{
                    players.remove(player);
                    mg.setPlayers(players);
                    driver.setGame(mg);
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void maybeDropGame(String gameName) {
        try{
            MongoGame mg = driver.getGame(gameName);
            if (mg.getPlayers().isEmpty()){
                //driver needs delete function.
            }
        }catch (Exception e){
            e.printStackTrace();

        }
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
        try{
            MongoGame mg = driver.getGame(gameName);
            if (mg != null){
                java.util.Map<String, Hand> hands = mg.getHands();
                Hand playerHand = hands.get(playerName);

                List<TrainCard> deck = mg.getTrainDeck();
                Random random = new Random();
                int randomCardIndex = random.nextInt() % deck.size();
                TrainCard card = deck.remove(randomCardIndex);
                playerHand.addTrainCard(card);

                hands.put(playerName, playerHand);
                mg.setHands(hands);
                mg.setTrainDeck(deck);
                driver.setGame(mg);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
        MongoGame currentGame = getGame(game_name);
        int playerId = getPlayerNumber(currentGame, username);
        int trainsLeft = -1;
        boolean result = false;

        City city1 = null;
        City city2 = null;

        for(PlayerOverview player : currentGame.getCheckpoint().getPlayers()) {
            if(player.getUsername().equals(username)) {
                trainsLeft = player.getPieces();
            }
        }

        for(Track track : currentGame.getCheckpoint().getMap().getTracks()) {
            if(track.getRoute_number() == route_number) {
                city1 = track.getCity1();
                city2 = track.getCity2();
                if(track.getOwner() == -1) {
                    if(trainsLeft > track.getLength()) {
                        result = true;
                    }
                }
            }
        }

        //Check if double routes are allowed...
        if(currentGame.getPlayers().size() > 3) {
            for(Track track : currentGame.getCheckpoint().getMap().getTracks()) {
                if(track.getRoute_number() != route_number && track.getCity1().equals(city1) && track.getCity2().equals(city2)) {
                    // If the person owns the other route return false
                    if(track.getOwner() == playerId) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean claimRoute(String game_name, String username, int route_number) {
        boolean result = false;
        MongoGame currentGame = getGame(game_name);
        for(Track track : currentGame.getCheckpoint().getMap().getTracks()) {
            if(track.getRoute_number() == route_number) {
                track.setOwner(getPlayerNumber(currentGame, username));
                result = true;
            }
        }
        return result;
    }

    @Override
    public void removeTrainsFromPlayer(String game_name, String username, int trainsToRemove) {
        MongoGame currentGame = getGame(game_name);

        for(PlayerOverview player : currentGame.getCheckpoint().getPlayers()) {
            if(player.getUsername().equals(username)) {
                player.setPieces(player.getPieces() - trainsToRemove);
            }
        }
    }

    @Override
    public List<Track> getTracks(String game_name) {
        return this.getGame(game_name).getCheckpoint().getMap().getTracks();
    }

    @Override
    public int numTrainsLeft(String game_name, String player_name) {
        MongoGame currentGame = getGame(game_name);
        int playerTrains=MAX_TRAIN;
        int playerNumber = this.getPlayerNumber(currentGame, player_name);

        for(Track track : currentGame.getCheckpoint().getMap().getTracks()) {
            if(track.getOwner() == playerNumber) {
                playerTrains = playerTrains-track.getLength();
            }
        }

        return playerTrains;
    }

    @Override
    public boolean addCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll) {
        try {
            MongoGame game = getGame(cmd.getGameName());
            game.getCommands().add(cmd);
            if (!(game.getCommands().size() - (game.getCheckpointIndex() + 1) == betweenCheckpoint)) {
                MongoGame oldgame = driver.getGame(cmd.getGameName());
                oldgame.getCommands().add(cmd);
                driver.setGame(oldgame);
            }else{
                game.setCheckpointIndex(game.getCheckpointIndex()+betweenCheckpoint);
                driver.setGame(game);
            }
            return true;
        }catch (UnknownHostException e){   }
        return false;
    }

    @Override
    public EndTurnCommand getEndTurnCommand(String gameName, int commandNumber, String playerName) {
        EndTurnCommand newEndTurn = new EndTurnCommand();
        MongoGame currentGame = getGame(gameName);
        int numPlayers = currentGame.getCheckpoint().getPlayers().size();
        int playerId = getPlayerNumber(currentGame, playerName);

        if(playerId > 0) {
            newEndTurn.setPreviousPlayer(playerId-1);
        }

        if(playerId < (numPlayers-1)) {
            newEndTurn.setNextPlayer(playerId+1);
        } else {
            newEndTurn.setNextPlayer(0);
        }

        return newEndTurn;
    }

    @Override
    public List<BaseCommand> getCommandsSinceLastCommand(String game_name, String player_name, int lastCommandID) {
        List<BaseCommand> remainingCommands = new ArrayList<BaseCommand>();
        MongoGame currentGame = getGame(game_name);
        for(BaseCommand command : currentGame.getCommands()) {
            if(command.getCommandNumber() > lastCommandID) {
                remainingCommands.add(command);
            }
        }
        return remainingCommands;
    }

    @Override
    public boolean validateCommand(BaseCommand command) {
        boolean valid = false;
        MongoGame currentGame = getGame(command.getGameName());

        int commandNumber = command.getCommandNumber();
        int lastCommandExecuted = currentGame.getCommands().get(currentGame.getCommands().size()-1).getCommandNumber();
        if(commandNumber == (lastCommandExecuted+1)) {
            valid = true;
        }

        return valid;
    }

    @Override
    public int getNumberOfDrawTrainCommands(String game_name) {
        MongoGame currentGame = getGame(game_name);
        int numberOfDrawCommands = 0;
        int indexOfLastEndTurn = 0;

        for(BaseCommand command : currentGame.getCommands()) {
            if(command.getName().equals("EndTurn")) {
                indexOfLastEndTurn = command.getCommandNumber();
            }
        }

        for(int i=indexOfLastEndTurn; i<currentGame.getCommands().size(); i++) {
            if(currentGame.getCommands().get(i).getName().equals("DrawTrainCard")) {
                numberOfDrawCommands++;
            }
        }

        return numberOfDrawCommands;
    }

    @Override
    public boolean isEndOfGame(String game_name) {
        int player = -1;
        boolean lastRound = false;

        MongoGame currentGame = this.getGame(game_name);
        for (BaseCommand command : currentGame.getCommands()) {
            if (lastRound && (command.getName().equals("EndTurn") && command.getPlayerNumber() == player)) {
                return true;
            } else if (command.getName().equals("LastTurn")) {
                player = command.getPlayerNumber();
                lastRound = true;
            }
        }

        return false;
    }

    @Override
    public boolean alreadyLastRound(String game_name) {
        MongoGame currentGame = this.getGame(game_name);
        for (BaseCommand command : currentGame.getCommands()) {
            if (command.getName().equals("LastTurn")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GameModel getGameModel(String game_name) {
        return ((MongoGame)mongoGames.get(game_name)).getCheckpoint();
    }

    @Override
    public void updateCurrentPlayer(String game_name, int nextPlayer) {
        getGameModel(game_name).setCurrentTurn(nextPlayer);

    }
}
