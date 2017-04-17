package edu.goldenhammer.database;

import edu.goldenhammer.model.City;
import edu.goldenhammer.model.GameModel;
import edu.goldenhammer.model.Player;
import edu.goldenhammer.model.Track;
import edu.goldenhammer.mongoStuff.MongoDriver;
import edu.goldenhammer.mongoStuff.MongoGame;
import edu.goldenhammer.mongoStuff.MongoUser;
import javafx.util.Pair;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by McKean on 4/17/2017.
 */

public class MongoUserDAO implements IUserDAO{
    private int MAX_TRAIN;
    private MongoDriver driver;

    private TreeMap<String, City> allCities;
    private TreeMap<Pair<City,City>,Track> allTracks;
    public static final int ROUTE_COUNT = 101;
    public static final int CITY_COUNT = 35;
    public static final int MAX_DESTINATION_CARDS = 76;


    private int betweenCheckpoint;


    private TreeMap mongoGames;

    public MongoUserDAO(int maxTrain, int betweenCheckpoint) {
        MAX_TRAIN=maxTrain;
        driver = new MongoDriver();
        mongoGames = new TreeMap<String, GameModel>();

        this.betweenCheckpoint = betweenCheckpoint;

    }

    public MongoUserDAO(){
        MAX_TRAIN=45;
        driver = new MongoDriver();
        mongoGames = new TreeMap<String, GameModel>();

        allCities = new TreeMap<>();

        betweenCheckpoint = 5;

    }
    @Override
    public Player getPlayerInfo(String player) {
        try{
            MongoUser user = driver.getUser(player);
            if (user == null){
                return null;
            }
            else{
                return new Player(player,user.getToken());
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean login(String username, String password) {
        MongoUser user;
        try{
            user = driver.getUser(username);
            return user!=null && user.getPassword().equals(password);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> getPlayers(String gameID) {
        try{
            MongoGame mg = driver.getGame(gameID);
            if (mg == null){
                return null;
            }
            else{
                return mg.getPlayers();
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean createUser(String username, String password) {
        MongoUser u = new MongoUser(username,password);
        try {
            if (driver.getUser(username) != null){
                return false;
            }
            else{
                driver.setUser(u);
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public void setAccessToken(String username, String accessToken) {
        try{
            MongoUser u = driver.getUser(username);
            u.setToken(accessToken);
            driver.setUser(u);
        }catch(Exception e){

        }
    }
}
