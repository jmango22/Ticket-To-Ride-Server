package edu.goldenhammer.server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.database.DatabaseController;
/**
 * Created by seanjib on 2/5/2017.
 */
public class LoginHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        try {
            String requestBody = readRequestBody(exchange);
            JsonObject credentials = Serializer.deserialize(requestBody);
            String username = credentials.get("username").getAsString();
            String password = credentials.get("password").getAsString();

            DatabaseController dbc = DatabaseController.getInstance();
            boolean success = dbc.login(username, password);

            Results result = new Results();
            if(success) {
                String access_token = dbc.getPlayerInfo(username).getAccessToken();

                if(!access_token.isEmpty()) {
                    result.setResponseCode(200);
                    result.setMessage(access_token);
                }
                else {
                    result.setResponseCode(500);
                    result.setMessage("Error: something went wrong. There is no access token for this user.");
                }
            }
            else {
                result.setResponseCode(500);
                result.setMessage("Error: bad credentials");
            }
            sendResponse(exchange, result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
