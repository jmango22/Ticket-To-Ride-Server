package edu.goldenhammer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.model.IGameModel;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

/**
 * Created by seanjib on 2/5/2017.
 */
public class PlayGameHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        try {
            DatabaseController dbc = DatabaseController.getInstance();
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

                    IGameModel game = dbc.playGame(gamename);
                    if (game != null) {
                        results.setResponseCode(200);
                        results.setMessage(Serializer.serialize(game));
                    } else {
                        results.setResponseCode(400);
                        results.setAndSerializeMessage("Error: cannot open game");
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