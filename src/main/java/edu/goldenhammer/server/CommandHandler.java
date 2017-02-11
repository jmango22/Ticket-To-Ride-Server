package edu.goldenhammer.server;

import com.sun.net.httpserver.HttpExchange;
import edu.goldenhammer.commands.BaseCommand;

import java.io.IOException;

public class CommandHandler extends HandlerBase {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestBody = readRequestBody(httpExchange);
            Class c = this.getClass();
            String pkg = c.getPackage().getName();
            BaseCommand baseCommand = Serializer.deserializeCommand(
                    requestBody, pkg + ".Server");
            Results result = baseCommand.execute();
            sendResponse(httpExchange, result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
