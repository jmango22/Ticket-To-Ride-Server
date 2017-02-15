package edu.goldenhammer.server.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.io.IOException;
import java.util.Random;

public class RegisterHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        int responseCode = 404;
        String message = "{\"message\":\"Error: user exists\"}";
        try {
            try {
                String requestBody = readRequestBody(exchange);
                JsonObject credentials = Serializer.deserialize(requestBody);
                String username = credentials.get("username").getAsString();
                String password = credentials.get("password").getAsString();

                DatabaseController dbc = DatabaseController.getInstance();
                boolean success = dbc.createUser(username, password);


                if (success) {
                    dbc.setAccessToken(username, Integer.toString(new Random().nextInt(100000000)));
                    String access_token = dbc.getPlayerInfo(username).getAccessToken();
                    responseCode = 200;
                    message = String.format("{\"authorization\":\"%1$s\"}",access_token);
                }
            } catch (Exception e){
                responseCode = 400;
                message = "{\"message\":\"Error: need username and password\"}";
            }
                Results result = new Results();
                result.setResponseCode(responseCode);
                result.setMessage(message);
                sendResponse(exchange,result);

//            sendResponse(exchange, result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}