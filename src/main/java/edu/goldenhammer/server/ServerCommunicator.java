package edu.goldenhammer.server;

import java.io.*;
import java.net.*;
import java.util.List;

import com.sun.net.httpserver.*;
import edu.goldenhammer.database.*;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.handlers.*;

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

    public static void main(String[] args) throws Exception {

//        DatabaseController c = DatabaseController.getInstance();
//        c.joinGame("dk","ghteam");
//        c.leaveGame("dk","ghteam");
//        DatabaseController dbc = DatabaseController.getInstance();
//        List<BaseCommand> commandList;
//        commandList = dbc.getCommandsSinceLastCommand("just", "devon1", 0);
//        dbc.getTracks("aaaa");
        printClasspath();
        int numTrains = 45;
        String persistenceType = "";
        String clearOrCheckpointLength = "5";
        String portNumber = "8082";//args[0];
        if(args.length > 0)
            portNumber = args[0];
        if(args.length > 1)
            numTrains = Integer.parseInt(args[1]);
        if (args.length >2)
            persistenceType = args[2];
        if (args.length > 3)
            clearOrCheckpointLength = args[3];
        persistenceType = "mongo";
        AbstractFactory factory;
        if (persistenceType.equals("mongo")){
            ExtensionLoader<AbstractFactory> factoryLoader = new ExtensionLoader<>();
            factory =factoryLoader.LoadClass("/plugins", "edu.goldenhammer.database.MongoFactory", AbstractFactory.class);
        }
        else {
            ExtensionLoader<AbstractFactory> factoryLoader = new ExtensionLoader<>();
            factory =factoryLoader.LoadClass("/plugins", "edu.goldenhammer.database.SQLFactory", AbstractFactory.class);
        }



        IGameDAO gameDAO = factory.getGameDAO();
        IUserDAO userDAO = factory.getUserDAO();

        gameDAO.setMaxTrains(numTrains);

        DatabaseController.setGameDAO(gameDAO);
        DatabaseController.setUserDAO(userDAO);

        if (clearOrCheckpointLength == "clear"){
            gameDAO.clear();
            userDAO.clear();
        }
        else{
            int checkpointLength = Integer.parseInt(clearOrCheckpointLength);
            gameDAO.setCheckpointLength(checkpointLength);
        }
//        DatabaseController.setFirstInstance(numTrains);
        System.out.println("Running on port: " + portNumber);
        new ServerCommunicator().run(portNumber);
//        SQLConnectionFactory factory = SQLConnectionFactory.getInstance();
//        Connection conn = factory.getConnection();
//        String sqlString = "select * from advisor";//String.format("SELECT %1$s FROM %2$s order by year ASC, semester ASC", "*", "students");
//        try {
//            PreparedStatement statement = conn.prepareStatement(sqlString);
//            ResultSet resultSet = statement.executeQuery();
//            System.out.println(resultSet.toString());
//        } catch (Exception e){
//            e.printStackTrace();
//        }
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
        server.createContext("/getcommands", new GetCommandsHandler());
        server.createContext("/getmessages", new GetMessagesHandler());
        server.createContext("/postmessage", new PostMessageHandler());
    }

    public static void printClasspath() {
        try {
            ExtensionLoader<IGameDAO> loader = new ExtensionLoader<>();
            IGameDAO controller = loader.LoadClass("/plugins", "edu.goldenhammer.database.MongoGameDAO", IGameDAO.class);
            System.out.println(controller.getGames());
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Get the System Classloader
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        // Get the URLs
        URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
        System.out.println(urls.length);
        for (int i = 0; i < urls.length; i++) {
            System.err.println(urls[i].getFile());
        }

    }
}
