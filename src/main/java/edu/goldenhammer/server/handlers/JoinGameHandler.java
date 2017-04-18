package edu.goldenhammer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.IGameDAO;
import edu.goldenhammer.server.Results;

/**
 * Created by seanjib on 2/5/2017.
 *
 * Header includes authorization token, username, and game name
 * Response indicates success
 */
public class JoinGameHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        try {
            IGameDAO dbc = DatabaseController.getGameDAO();
            Results results = new Results();

            //verify username and access token
            if(!isAuthorized(exchange)) {
                results = getInvalidAuthorizationResults();
            }
            else {
                //Get games either for a specific user or get all the games at once

                if(exchange.getRequestHeaders().containsKey("username")
                        && exchange.getRequestHeaders().containsKey("gamename")) {
                    String username = exchange.getRequestHeaders().get("username").get(0);
                    String gamename = exchange.getRequestHeaders().get("gamename").get(0);

                    boolean success = dbc.joinGame(username, gamename);

                    if (success) {
                        results.setResponseCode(200);
                        results.setAndSerializeMessage("Game " + gamename + " successfully joined!");
                    } else {
                        results.setResponseCode(400);
                        results.setAndSerializeMessage("Error: cannot join game");
                    }
                }
                else {
                    results.setResponseCode(400);
                    results.setAndSerializeMessage("Error: Invalid username or game name included in URL");
                }
            }
            sendResponse(exchange, results);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}