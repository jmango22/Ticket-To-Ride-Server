package edu.goldenhammer.server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by seanjib on 2/5/2017.
 */
public class CreateGameHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        try {
            String requestBody = readRequestBody(exchange);

            DatabaseController dbc = DatabaseController.getInstance();
            Results results = new Results();

            //verify username and access token
            if(!isAuthorized(exchange)) {
                results = getInvalidAuthorizationResults();
            }
            else {
                JsonObject gameNameJson = Serializer.deserialize(requestBody);
                String gameName = gameNameJson.get("name").getAsString();

                boolean success = dbc.createGame(gameName);

                if (success) {
                    results.setResponseCode(200);
                    results.setMessage("Game successfully created!");
                } else {
                    results.setResponseCode(500);
                    results.setMessage("Error: game already exists");
                }
            }
            sendResponse(exchange, results);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}