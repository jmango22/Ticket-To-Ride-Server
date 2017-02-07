package edu.goldenhammer.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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
        String response = Serializer.serialize(results);
        httpExchange.sendResponseHeaders(results.getResponseCode(), response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected boolean isAuthorized(HttpExchange exchange) {
        String access_token = exchange.getRequestHeaders().get("Authorization").get(0);
        String username = exchange.getRequestHeaders().get("Username").get(0);
        DatabaseConnection dbc = DatabaseConnection.getInstance();
        return dbc.authorize(username).equals(access_token);
    }

    protected Results getInvalidAuthorizationResults() {
        Results results = new Results();
        results.setResponseCode(500);
        results.setMessage("Error: invalid username or invalid access token.");

        return results;
    }

    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
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
