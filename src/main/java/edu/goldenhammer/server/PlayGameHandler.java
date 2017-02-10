package edu.goldenhammer.server;

import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.data_types.IServerGame;
import edu.goldenhammer.database.DatabaseController;

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

                    IServerGame game = dbc.playGame(username, gamename);

                    if (game != null) {
                        results.setResponseCode(200);
                        results.setMessage(Serializer.serialize(game));
                    } else {
                        results.setResponseCode(500);
                        results.setMessage("Error: cannot open game");
                    }
                }
                else {
                    results.setResponseCode(500);
                    results.setMessage("Error: Invalid username or game name included in URL");
                }
            }
            sendResponse(exchange, results);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}