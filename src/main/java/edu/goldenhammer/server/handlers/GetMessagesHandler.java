package edu.goldenhammer.server.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.data_types.DatabaseMessage;
import edu.goldenhammer.model.Message;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanjib on 3/9/2017.
 */
public class GetMessagesHandler extends HandlerBase{
    public void handle(HttpExchange exchange) {
        Results results = new Results();
        try {
            String requestBody = readRequestBody(exchange);
            IDatabaseController dbc = DatabaseController.getInstance();

            if (!isAuthorized(exchange)) {
                results = getInvalidAuthorizationResults();
            } else {
                String game_name = exchange.getRequestHeaders().get("gamename").get(0);
                List<Message> messages = parseDatabaseMessages(dbc.getMessages(game_name));

                results.setResponseCode(200);
                results.setMessage(Serializer.serialize(messages));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            results.setResponseCode(400);
            results.setAndSerializeMessage("Error: Cannot input message into database");
        }
        try {
            sendResponse(exchange, results);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static List<Message> parseDatabaseMessages(List<DatabaseMessage> databaseMessages) {
        List<Message> messages = new ArrayList<>();
        IDatabaseController dbc = DatabaseController.getInstance();
        for(int i = 0; i < databaseMessages.size(); i++) {
            DatabaseMessage databaseMessage = databaseMessages.get(i);
            messages.add(new Message(dbc.getUsername(databaseMessage.getPlayerID()), databaseMessage.getMessage()));
        }
        return messages;
    }
}
