package edu.goldenhammer.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

/**
 * Created by seanjib on 2/5/2017.
 */
public class LoginHandler extends HandlerBase {
    public void handle(HttpExchange exchange) {
        String message = "{\"message\":\"Error: bad credentials\"";
        int responseCode = 400;
        try {
            String requestBody = readRequestBody(exchange);
            JsonObject credentials = Serializer.deserialize(requestBody);
            String username = credentials.get("username").getAsString();
            String password = credentials.get("password").getAsString();

            DatabaseController dbc = DatabaseController.getInstance();
            boolean success = dbc.login(username, password);


            if(success) {
                //todo: maybe we should always create a new accessToken
                dbc.setAccessToken(username, Integer.toString(new Random().nextInt(100000000)));
                String access_token = dbc.getPlayerInfo(username).getAccessToken();

                if(!access_token.isEmpty()) {
                    responseCode =200;
                    Gson g = new Gson();
                    message = String.format("{\"authorization\":\"%1$s\"}",access_token);
                }
            }

        } catch (Exception ex) {

        }
        try{
            Results result = new Results();
            result.setMessage(message);
            result.setResponseCode(responseCode);
            sendResponse(exchange, result);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
