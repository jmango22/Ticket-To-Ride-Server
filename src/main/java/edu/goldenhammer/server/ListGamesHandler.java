package edu.goldenhammer.server;

import com.sun.net.httpserver.HttpExchange;

import edu.goldenhammer.model.GameList;
import edu.goldenhammer.database.data_types.IServerPlayer;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.model.IGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by seanjib on 2/5/2017.
 */
public class ListGamesHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        try {
            Results results = new Results();
            if(!isAuthorized(exchange)) {
                results = getInvalidAuthorizationResults();
            }
            else {
                DatabaseController dbc = DatabaseController.getInstance();
                GameList gameList;

                Map<String, String> parameters = queryToMap(exchange.getRequestURI().getQuery());

                //Get games either for a specific user or get all the games at once
                if(parameters.containsKey("username")) {
                    String username = parameters.get("username");
                    gameList = dbc.getGames(username);
                }
                else {
                    gameList = dbc.getGames();
                }

                //Make sure gameList was actually populated with data, even if there are no games
                if(gameList == null) {
                    results.setResponseCode(500);
                    results.setMessage("ERROR: invalid username in URL");
                }
                else {
                    for(IGame item : gameList.getGameList()) {
                        List<IServerPlayer> players = dbc.getPlayers(item.getID()); //gets the list of players for the given game
                        List<String> playerUsernames = new ArrayList<>();
                        for(IServerPlayer player : players) {                       //converts the list of players to a list of usernames
                            playerUsernames.add(player.getUsername());
                        }
                        item.setPlayers(playerUsernames);
                    }
                    results.setResponseCode(200);
                    results.setMessage(Serializer.serialize(gameList));
                }
            }
            sendResponse(exchange, results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
