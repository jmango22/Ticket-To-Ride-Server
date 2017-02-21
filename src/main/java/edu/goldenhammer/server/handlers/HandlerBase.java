package edu.goldenhammer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.data_types.IDatabasePlayer;
import edu.goldenhammer.server.Results;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public abstract class HandlerBase implements HttpHandler {

    protected String readRequestBody(HttpExchange httpExchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader is = new InputStreamReader(httpExchange.getRequestBody());
        char[] buf = new char[1024];
        int length;
        while((length = is.read(buf)) > 0) {
            sb.append(buf, 0, length);
        }

        return sb.toString();
    }

    protected void sendResponse(HttpExchange httpExchange, Results results) throws IOException{
        String response = results.getMessage();
        httpExchange.getResponseHeaders().add("Content-Type","application/json");
        httpExchange.sendResponseHeaders(results.getResponseCode(), response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected boolean isAuthorized(HttpExchange exchange) {
        String access_token = exchange.getRequestHeaders().get("Authorization").get(0);
        String username = exchange.getRequestHeaders().get("Username").get(0);
        DatabaseController dbc = DatabaseController.getInstance();
        IDatabasePlayer player = dbc.getPlayerInfo(username);
        return player.getAccessToken().equals(access_token);
    }

    protected Results getInvalidAuthorizationResults() {
        Results results = new Results();
        results.setResponseCode(500);
        results.setAndSerializeMessage("Error: invalid username or invalid access token.");

        return results;
    }

    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        if(query == null){
            return result;
        }
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
