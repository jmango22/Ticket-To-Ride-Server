package edu.goldenhammer.server;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
/**
 * Created by devonkinghorn on 1/19/17.
 */
public class ServerCommunicator {
    private static final int MAX_WAITING_CONNECTIONS = 12;

    private HttpServer server;

    private void run(String portNumber) {
        System.out.println("Initializing HTTP Server");
        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.setExecutor(null); // use the default executor

        System.out.println("Creating contexts");
        createContexts(server);

        System.out.println("Starting server");
        server.start();
    }

    public static void main(String[] args) {
        String portNumber = "8081";//args[0];
        new ServerCommunicator().run(portNumber);
    }

    private static void createContexts(HttpServer server) {
        server.createContext("/commands", new CommandHandler());
        server.createContext("/creategame", new CreateGameHandler());
        server.createContext("/joingame", new JoinGameHandler());
        server.createContext("/leavegame", new LeaveGameHandler());
        server.createContext("/listofgames", new ListGamesHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/playgame", new PlayGameHandler());
        server.createContext("/register", new RegisterHandler());
    }
}
