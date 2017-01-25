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
//        server.createContext("/commands", new CommandHandler());

        System.out.println("Starting server");
        server.start();
    }

    public static void main(String[] args) {
        String portNumber = "8080";//args[0];
        new ServerCommunicator().run(portNumber);
    }
}
