package edu.goldenhammer.server;

import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.data_types.GameList;

import java.io.IOException;
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
                DatabaseConnection dbc = DatabaseConnection.getInstance();
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
