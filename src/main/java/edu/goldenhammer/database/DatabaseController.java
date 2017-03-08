package edu.goldenhammer.database;

import edu.goldenhammer.database.data_types.*;
import edu.goldenhammer.model.*;
import edu.goldenhammer.server.Serializer;
import edu.goldenhammer.server.commands.BaseCommand;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Created by devonkinghorn on 2/4/17.
 */
public class DatabaseController implements IDatabaseController {

    private static DatabaseController singleton;
    private DatabaseConnectionFactory session;

    /**
     * @pre The PSQL database is able to connect with the credentials in the config
     *
     * @post an instance of DatabaseController is returned with a connection to the SQL database
     * @return an instance of DatabaseController
     */
    public static DatabaseController getInstance(){
        if(singleton == null)
            singleton = new DatabaseController();
        return singleton;
    }


    /**
     * @pre The PSQL database is able to connect with the credentials in the config
     *
     * @post an instance of DatabaseController is returned with a connection to the SQL database
     */
    public DatabaseController(){
        initializeDatabase();
    }

    private void initializeDatabase() {
        this.session = DatabaseConnectionFactory.getInstance();
        ensureTablesCreated();
    }

    private void ensureTablesCreated() {
        createTable(DatabaseGame.CREATE_STMT);
        createTable(DatabasePlayer.CREATE_STMT);
        createTable(DatabaseParticipants.CREATE_STMT);
        createTable(DatabaseCity.CREATE_STMT);
        createTable(DatabaseRoute.CREATE_STMT);
        createTable(DatabaseClaimedRoute.CREATE_STMT);
        createTable(DatabaseDestinationCard.CREATE_STMT);
        createTable(DatabaseTrainCard.CREATE_STMT);
        createTable(DatabaseCommand.CREATE_STMT);
        createTable(DatabaseMessage.CREATE_STMT);
        initializeCities();
        initializeRoutes();
    }

    private void createTable(String sqlStatementString) {
        try (Connection connection = session.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sqlStatementString);
            statement.execute();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * @param player_user_name the username associated with the IDatabasePlayer to be returned
     * @pre the database connection is valid and the player_user_name is associated with a user already stored and the database schema has not been altered
     * @post The IDatabasePlayer is read from the database
     * @return The IDatabasePlayer associated with the username is returned
     */
    @Override
    public IDatabasePlayer getPlayerInfo(String player_user_name) {
        try (Connection connection = session.getConnection()){
            String sqlString = String.format("SELECT %1$s FROM %2$s WHERE %3$s = ?",
                    DatabasePlayer.columnNames(),
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, player_user_name);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                return new DatabasePlayer(
                        resultSet.getString(DatabasePlayer.ID),
                        resultSet.getString(DatabasePlayer.USERNAME),
                        resultSet.getString(DatabasePlayer.PASSWORD),
                        resultSet.getString(DatabasePlayer.ACCESS_TOKEN)
                );
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private GameList getGameListFromResultSet (ResultSet resultSet) throws SQLException{
        GameList gameList = new GameList();
        GameListItem game = null;
        while(resultSet.next()){
            String user_id = resultSet.getString((DatabasePlayer.USERNAME));
            String game_id = resultSet.getString(DatabaseParticipants.GAME_ID);
            if(game == null || !game_id.equals(game.getID())){

                String name = resultSet.getString(DatabaseGame.GAME_NAME);
                game = new GameListItem(game_id, name, false, new ArrayList<>());
                gameList.add(game);
            }
            game.getPlayers().add(user_id);
        }
        return gameList;
    }

    /**
     * @pre the connection to the SQL database is valid and the database schema has not been altered
     * @post all games that are not started are returned
     * @return all games that have not been started
     */
    @Override
    public GameList getGames() {

        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s, %2$s, %3$s FROM %4$s NATURAL JOIN %5$s " +
                            "NATURAL JOIN %6$s WHERE %7$s = false ORDER BY %8$s",
                    DatabaseParticipants.columnNames(),
                    DatabaseGame.GAME_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseGame.TABLE_NAME,
                    DatabasePlayer.TABLE_NAME,
                    DatabaseGame.STARTED,
                    DatabaseGame.ID);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            ResultSet resultSet = statement.executeQuery();
            return getGameListFromResultSet(resultSet);
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @param player_user_name the name of the user to find all games associated with
     * @pre the connection to the SQL database is valid
     * @post all games in which the player_user_name are involved is returned
     * @return all games in which the player is a participant
     */
    @Override
    public GameList getGames(String player_user_name) {

        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s, %2$s, %3$s FROM %4$s NATURAL JOIN %5$s\n" +
                            "NATURAL JOIN %6$s WHERE %7$s IN (SELECT %8$s FROM %9$s\n" +
                            "WHERE %10$s IN (SELECT %11$s FROM %12$s WHERE %13$s=?)) ORDER BY %14$s",
                    DatabaseParticipants.columnNames(),
                    DatabaseGame.GAME_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseGame.TABLE_NAME,
                    DatabasePlayer.TABLE_NAME,
                    DatabaseGame.ID,
                    DatabaseParticipants.GAME_ID,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.USER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseGame.ID);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,player_user_name);
            ResultSet resultSet = statement.executeQuery();
            return getGameListFromResultSet(resultSet);
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param username users username for the person logging in
     * @param password the password for the person logging in
     * @pre the database schema has not been altered
     * @post the method will return true if a record with the same username and password exist in the database
     * @return returns true if the player exists else false
     */
    @Override
    public Boolean login(String username, String password) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s WHERE %3$s = ? AND %4$s = ?",
                    DatabasePlayer.columnNames(),
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabasePlayer.PASSWORD);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,username);
            statement.setString(2,password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); //if there is one result for the username and password input, it is a valid login
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param username the username wanting to be stored for a new user
     * @param password the password to be used for logging in later.
     * @pre the database schema has not been altered, the username is not in use
     * @post the associated username and password will be added to the database.
     * @return if the new user was created true, else false is returned
     */
    @Override
    public Boolean createUser(String username, String password) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s,%3$s) VALUES (?,?)",
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabasePlayer.PASSWORD);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,username);
            statement.setString(2,password);
            return  statement.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param name the name the new game should have.
     * @pre the database schema has not been altered, and another game with the same name doesn't exist
     * @post a new game will be created with the name.
     * @return if a game with that name was created
     */
    @Override
    public Boolean createGame(String name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s,%3$s) VALUES (?,?)",
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,
                    DatabaseGame.STARTED);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,name);
            statement.setBoolean(2,false);
            return  statement.executeUpdate() > 0;
        } catch(SQLException e){
        }
        return false;
    }

    /**
     *
     * @param player_name username of the player to be added to the game
     * @param game_name the name of the game the player above should be added to.
     * @pre the database schema has not been altered, The game exists with fewer than 5 participants and has not yet started, and the player exists
     * @post the player will be added to the game.
     * @return returns true if the player was added to the game else it returns false.
     */
    @Override
    public Boolean joinGame(String player_name, String game_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT count(*) FROM (\n" +
                    "SELECT * FROM %1$s INNER JOIN (\n" +
                    "SELECT %2$s FROM %3$s WHERE %4$s = ? AND %5$s = false\n" +
                    ") AS game_ids ON (%1$s.%6$s = game_ids.%2$s)\n" +
                    ") AS participant_count;",
                    DatabaseParticipants.TABLE_NAME,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,
                    DatabaseGame.STARTED,

                    DatabaseParticipants.GAME_ID);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,game_name);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                int currentParticipantsCount = resultSet.getInt(1);
                if(currentParticipantsCount < 5) {
                    sqlString = String.format("INSERT INTO %1$s(%2$s, %3$s)" +
                            "VALUES (" +
                                    "(SELECT %4$s FROM %5$s WHERE %6$s = ?)," +
                                    "(SELECT %7$s FROM %8$s WHERE %9$s =  ?)" +
                            ");",
                            DatabaseParticipants.TABLE_NAME,
                            DatabaseParticipants.USER_ID,
                            DatabaseParticipants.GAME_ID,

                            DatabasePlayer.ID,
                            DatabasePlayer.TABLE_NAME,
                            DatabasePlayer.USERNAME,

                            DatabaseGame.ID,
                            DatabaseGame.TABLE_NAME,
                            DatabaseGame.GAME_NAME);
                    statement = connection.prepareStatement(sqlString);
                    statement.setString(1, player_name);
                    statement.setString(2, game_name);

                    return (statement.executeUpdate() != 0);
                }
            }

        } catch (SQLException e) {
        }
        return false;
    }


    private List<String> getPlayerNamesFromResultSet (ResultSet resultSet) throws SQLException{
        List<String> playerList = new ArrayList<>();
        while(resultSet.next()){
            String username = resultSet.getString((DatabasePlayer.USERNAME));
            playerList.add(username);
        }
        return playerList;
    }

    /**
     *
     * @param game_name the name of the game to find players associated with
     * @pre the database schema has not been altered
     * @post all players involved in a game will be returned.
     * @return the list of usernames of players that are a member of the game
     */
    @Override
    public List<String> getPlayers(String game_name) {
        try (Connection connection = session.getConnection()) {

            //get the information to make the GameModel object from the database
            String sqlString = String.format("SELECT %1$s,%2$s FROM %3$s NATURAL JOIN %4$s\n" +
                            "WHERE %5$s IN (SELECT %6$s FROM %7$s WHERE %8$s = ?)",
                    DatabasePlayer.ID,
                    DatabasePlayer.USERNAME,
                    DatabasePlayer.TABLE_NAME,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,game_name);
            ResultSet resultSet = statement.executeQuery();

            return getPlayerNamesFromResultSet(resultSet);
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param player_name the player username to leave the game
     * @param game_name the name of the game to remove the player from
     * @pre the player is part of the game and the game has not yet started
     * @post the player will be removed from the game
     * @return returns true if the player left the game else false
     */
    @Override
    public Boolean leaveGame(String player_name, String game_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("DELETE FROM %1$s WHERE " +
                    "%2$s IN (SELECT %3$s FROM %4$s WHERE %5$s = ?)" +
                    "AND %6$s IN (SELECT %7$s FROM %8$s WHERE %9$s = ?)",
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.USER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,

                    DatabaseParticipants.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,player_name);
            statement.setString(2,game_name);
            return (statement.executeUpdate() != 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param game_name the name of the game to start
     * @pre the game has not yet started
     * @post the game will begin and no one will be able to join the game anymore
     * @return the IGameModel associated with the game
     */
    @Override
    public IGameModel playGame(String game_name) {
        try (Connection connection = session.getConnection()) {
            //get the information to make the GameModel object from the database
            String sqlString = String.format("SELECT %1$s FROM %2$s WHERE %3$s = ?",
                    DatabaseGame.STARTED,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,game_name);
            ResultSet resultSet = statement.executeQuery();

            List<String> players = getPlayers(game_name);
            if(resultSet.next() && players.size() > 1) {
                if(!resultSet.getBoolean(DatabaseGame.STARTED)){
                    initializeGame(game_name);
                }
                IGameModel gameModel = getGameModel(game_name);
                return gameModel;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public IGameModel getGameModel(String game_name) {
        List<PlayerOverview> players = getPlayerOverviews(game_name);
        List<DestinationCard> destinationDeck = getDestinationCards(game_name);
        List<TrainCard> trainCardDeck = getTrainCards(game_name);
        Map map = getMap(game_name);
        GameName gameName = new GameName(game_name);
        return new GameModel(players, destinationDeck, trainCardDeck, map, gameName);
    }

    private List<PlayerOverview> getPlayerOverviews(String game_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %5$s.%1$s, %5$s.%2$s, %5$s.%3$s, %5$s.%4$s, usernames.%8$s\n" +
                            "FROM %5$s INNER JOIN (SELECT %7$s, %8$s FROM %9$s) AS usernames\n" +
                            "ON %5$s.%6$s = usernames.%7$s\n" +
                            "WHERE %5$s.%10$s IN (SELECT %11$s FROM %12$s WHERE %13$s = ?)",
                    DatabaseParticipants.USER_ID,
                    DatabaseParticipants.PLAYER_NUMBER,
                    DatabaseParticipants.POINTS,
                    DatabaseParticipants.TRAINS_LEFT,

                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.USER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.USERNAME,

                    DatabasePlayer.TABLE_NAME,
                    DatabaseParticipants.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,

                    DatabaseGame.GAME_NAME
                    );
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            ResultSet resultSet = statement.executeQuery();

            List<PlayerOverview> players = new ArrayList<>();
            while(resultSet.next()) {
                Color color = Color.getPlayerColorFromNumber(resultSet.getInt(DatabaseParticipants.PLAYER_NUMBER));
                int pieces = resultSet.getInt(DatabaseParticipants.TRAINS_LEFT);
                int destCards = getDestinationCardCount(resultSet.getInt(DatabaseParticipants.USER_ID));
                int player = resultSet.getInt(DatabaseParticipants.PLAYER_NUMBER);
                String username = resultSet.getString(DatabasePlayer.USERNAME);
                int points = resultSet.getInt(DatabaseParticipants.POINTS);

                players.add(new PlayerOverview(color, pieces, destCards, player, username, points));
            }
            return players;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int getDestinationCardCount(int player_id) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT count(*) FROM %1$s WHERE %2$s = ?",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.PLAYER_ID);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setInt(1, player_id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private List<DestinationCard> getDestinationCards(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s.%2$s,\n" +
                            "city1.%9$s AS city1_point_x," +
                            "city1.%10$s AS city1_point_y,\n" +
                            "city1.%11$s AS city1_name,\n" +
                            "%1$s.%3$s,\n" +
                            "city2.%9$s AS city2_point_x,\n" +
                            "city2.%10$s AS city2_point_y,\n" +
                            "city2.%11$s AS city2_name, \n" +
                            "%1$s.%4$s \n" +
                            "FROM %1$s\n" +
                            "INNER JOIN %7$s AS city1 ON %1$s.%2$s = city1.%8$s \n" +
                            "INNER JOIN %7$s AS city2 ON %1$s.%3$s = city2.%8$s \n" +
                            "WHERE %5$s = ? AND %6$s = ?",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.CITY_1,
                    DatabaseDestinationCard.CITY_2,
                    DatabaseDestinationCard.POINTS,
                    DatabaseDestinationCard.DISCARDED,
                    DatabaseDestinationCard.DRAWN,

                    DatabaseCity.TABLE_NAME,
                    DatabaseCity.ID,
                    DatabaseCity.POINT_X,
                    DatabaseCity.POINT_Y,
                    DatabaseCity.NAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, false);
            statement.setBoolean(2, false);
            ResultSet resultSet = statement.executeQuery();

            List<DestinationCard> destinationCards = new ArrayList<>();
            while(resultSet.next()) {
                String name_1 = resultSet.getString("city1_name");
                int x_location_1 = resultSet.getInt("city1_point_x");
                int y_location_1 = resultSet.getInt("city1_point_y");

                String name_2 = resultSet.getString("city2_name");
                int x_location_2 = resultSet.getInt("city2_point_x");
                int y_location_2 = resultSet.getInt("city2_point_y");

                City city1 = new City(x_location_1, y_location_1, name_1);
                City city2 = new City(x_location_2, y_location_2, name_2);

                int pointsWorth = resultSet.getInt(DatabaseDestinationCard.POINTS);
                destinationCards.add(new DestinationCard(city1, city2, pointsWorth));
            }
            return destinationCards;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<TrainCard> getTrainCards(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s \n" +
                    "WHERE %3$s IS NULL\n" +
                    "AND %4$s = false\n" +
                    "AND %5$s = (SELECT %6$s FROM %7$s WHERE %8$s = ?);",
                    DatabaseTrainCard.TRAIN_TYPE,
                    DatabaseTrainCard.TABLE_NAME,
                    DatabaseTrainCard.PLAYER_ID,
                    DatabaseTrainCard.DISCARDED,
                    DatabaseTrainCard.GAME_ID,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);

            ResultSet resultSet = statement.executeQuery();

            List<TrainCard> trainCards = new ArrayList<>();
            while(resultSet.next()) {
                Color color = Color.getTrainCardColorFromString(resultSet.getString(DatabaseTrainCard.TRAIN_TYPE));
                trainCards.add(new TrainCard(color));
            }
            return trainCards;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Map getMap(String game_name) {
        List<Track> tracks = getTracks(game_name);
        List<City> cities = getCities();

        return new Map(tracks, cities);
    }

    private List<Track> getTracks(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s.%3$s,\n" +
                            "city1.%8$s AS city1_point_x, city1.%9$s AS city1_point_y, city1.%10$s AS city1_name,\n" +
                            "%1$s.%4$s,\n" +
                            "city2.%8$s AS city2_point_x, city2.%9$s AS city2_point_y, city2.%10$s AS city2_name,\n" +
                            "%1$s.%5$s, %1$s.%22$s, route_player_numbers.%18$s FROM %1$s\n" +
                            "INNER JOIN %6$s AS city1 ON %1$s.%3$s = city1.%7$s \n" +
                            "INNER JOIN %6$s AS city2 ON %1$s.%4$s = city2.%7$s\n" +
                            "LEFT JOIN (SELECT %11$s.%12$s, %11$s.%13$s,\n" +
                            "\t%11$s.%14$s, player_numbers.%18$s FROM %11$s\n" +
                            "\tINNER JOIN (SELECT %15$s.%16$s, %15$s.%17$s, %15$s.%18$s FROM %15$s\n" +
                            "\t\tWHERE %16$s = (SELECT %20$s FROM %19$s WHERE %21$s = ?))\n" +
                            "\t AS player_numbers\n" +
                            "\t ON %11$s.%12$s = player_numbers.%16$s\n" +
                            "\t AND %11$s.%13$s = player_numbers.%17$s)\n" +
                            "AS route_player_numbers\n" +
                            "ON route_player_numbers.%14$s = %1$s.%2$s;",
                    DatabaseRoute.TABLE_NAME, //1
                    DatabaseRoute.ROUTE_NUMBER,
                    DatabaseRoute.CITY_1,
                    DatabaseRoute.CITY_2,
                    DatabaseRoute.ROUTE_LENGTH,

                    DatabaseCity.TABLE_NAME, //6
                    DatabaseCity.ID,
                    DatabaseCity.POINT_X,
                    DatabaseCity.POINT_Y,
                    DatabaseCity.NAME,

                    DatabaseClaimedRoute.TABLE_NAME, //11
                    DatabaseClaimedRoute.GAME_ID,
                    DatabaseClaimedRoute.PLAYER_ID,
                    DatabaseClaimedRoute.ROUTE_ID,

                    DatabaseParticipants.TABLE_NAME, //15
                    DatabaseParticipants.GAME_ID,
                    DatabaseParticipants.USER_ID,
                    DatabaseParticipants.PLAYER_NUMBER,

                    DatabaseGame.TABLE_NAME, //19
                    DatabaseGame.ID,
                    DatabaseGame.GAME_NAME,

                    DatabaseRoute.ROUTE_COLOR);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);

            ResultSet resultSet = statement.executeQuery();

            List<Track> tracks = new ArrayList<>();
            while(resultSet.next()) {
                int location1x = resultSet.getInt("city1_point_x");
                int location1y = resultSet.getInt("city1_point_y");
                String cityName1 = resultSet.getString("city1_name");
                int location2x = resultSet.getInt("city2_point_x");
                int location2y = resultSet.getInt("city2_point_y");
                String cityName2 = resultSet.getString("city2_name");

                City city1 = new City(location1x, location1y, cityName1);
                City city2 = new City(location2x, location2y, cityName2);

                int length = resultSet.getInt(DatabaseRoute.ROUTE_LENGTH);
                Color color = Color.getTrackColorFromString(resultSet.getString(DatabaseRoute.ROUTE_COLOR));
                int owner = resultSet.getInt(DatabaseParticipants.PLAYER_NUMBER);

                tracks.add(new Track(city1, city2, length, color, owner, location1x, location1y, location2x, location2y));
            }
            return tracks;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<City> getCities() {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT * FROM %1$s",
                    DatabaseCity.TABLE_NAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            ResultSet resultSet = statement.executeQuery();

            List<City> cities = new ArrayList<>();
            while(resultSet.next()) {
                int x_location = resultSet.getInt(DatabaseCity.POINT_X);
                int y_location = resultSet.getInt(DatabaseCity.POINT_Y);
                String name = resultSet.getString(DatabaseCity.NAME);

                cities.add(new City(x_location, y_location, name));
            }
            return cities;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param player_user_name the username of the player to set the access token
     * @param accessToken the new access token to be associated with the player
     * @pre the username exists
     * @post the database will store the accessToken for the associated user
     */
    @Override
    public void setAccessToken(String player_user_name, String accessToken){
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %2$s = ? WHERE %3$s = ?",
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.ACCESS_TOKEN,
                    DatabasePlayer.USERNAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,accessToken);
            statement.setString(2,player_user_name);
            statement.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param gameName name of the game to possibly drop
     * @pre no one is participating in the game
     * @post the game will be dropped
     */
    @Override
    public void maybeDropGame(String gameName) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("DELETE FROM %1$s WHERE %2$s=?" +
                            "AND %3$s NOT IN (SELECT %4$s FROM %5$s)",
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,
                    DatabaseGame.ID,
                    DatabaseParticipants.GAME_ID,
                    DatabaseParticipants.TABLE_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,gameName);
            statement.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void initializeGame(String game_name) {
        initializeParticipants(game_name);
        initializeTrainCards(game_name);
        initializeDestinationCards(game_name);
        //initializePlayerTrainCards(game_name);
        setGameStarted(game_name);
    }

    private void setGameStarted(String game_name) {
        try (Connection connection = session.getConnection()){
            String sqlString = String.format("UPDATE %1$s SET %2$s = ? WHERE %3$s = ?",
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.STARTED,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, true);
            statement.setString(2, game_name);
            statement.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeParticipants(String game_name) {
        List<String> players = getPlayers(game_name);
        try (Connection connection = session.getConnection()){
            for (int i = 0; i < players.size(); i++) {
                String sqlString = String.format("UPDATE %1$s SET %2$s = ?, %3$s = 0, %4$s = ?" +
                                " WHERE %5$s IN (SELECT %6$s FROM %7$s WHERE %8$s = ?)" +
                                " AND %9$s IN (SELECT %10$s FROM %11$s WHERE %12$s = ?);",
                        DatabaseParticipants.TABLE_NAME,
                        DatabaseParticipants.PLAYER_NUMBER,
                        DatabaseParticipants.POINTS,
                        DatabaseParticipants.TRAINS_LEFT,

                        DatabaseParticipants.GAME_ID,
                        DatabaseGame.ID,
                        DatabaseGame.TABLE_NAME,
                        DatabaseGame.GAME_NAME,

                        DatabaseParticipants.USER_ID,
                        DatabasePlayer.ID,
                        DatabasePlayer.TABLE_NAME,
                        DatabasePlayer.USERNAME
                );

                PreparedStatement statement = connection.prepareStatement(sqlString);
                statement.setInt(1, i);
                statement.setInt(2, DatabaseParticipants.MAX_TRAIN_COUNT);
                statement.setString(3, game_name);
                statement.setString(4, players.get(i));
                statement.executeUpdate();

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeTrainCards(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s, %3$s, %4$s) VALUES %5$s",
                    DatabaseTrainCard.TABLE_NAME,
                    DatabaseTrainCard.ID,
                    DatabaseTrainCard.GAME_ID,
                    DatabaseTrainCard.TRAIN_TYPE,
                    DatabaseTrainCard.getAllTrainCards());

            PreparedStatement statement = connection.prepareStatement(sqlString);
            for(int i = 0; i < ((DatabaseTrainCard.MAX_COLORED_CARDS * 8) + DatabaseTrainCard.MAX_WILD_CARDS); i++) {
                statement.setInt(i * 2 + 1, i);
                statement.setString(i * 2 + 2, game_name);
            }
            statement.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeDestinationCards(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s, %3$s, %4$s, %5$s) VALUES\n%6$s",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.GAME_ID,
                    DatabaseDestinationCard.CITY_1,
                    DatabaseDestinationCard.CITY_2,
                    DatabaseDestinationCard.POINTS,
                    DatabaseDestinationCard.getAllDestinations());

            PreparedStatement statement = connection.prepareStatement(sqlString);
            String pathName = System.getProperty("user.dir") + "/src/main/res/destinations.txt";
            Scanner destinations = new Scanner(new File(pathName));
            for(int i = 0; i < DatabaseDestinationCard.MAX_DESTINATION_CARDS * 4; i += 4) {
                String destination = destinations.nextLine();
                String[] vars = destination.split(",");
                statement.setString(i + 1, game_name);
                statement.setString(i + 2, vars[0]);
                statement.setString(i + 3, vars[1]);
                statement.setInt(i + 4, Integer.parseInt(vars[2]));
            }
            statement.execute();

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void initializePlayerTrainCards(String game_name) {
        final int INITIAL_TRAIN_CARD_COUNT = 4;
        List<String> players = getPlayers(game_name);

        for (String player_name : players) {
            for (int i = 0; i < INITIAL_TRAIN_CARD_COUNT; i++) {
                DatabaseTrainCard trainCard = getRandomTrainCard(game_name);
                assignTrainCardToPlayer(trainCard, player_name);
            }
        }
    }

    private void initializeCities() {
        try(Connection connection = session.getConnection()) {
            String sqlString = DatabaseCity.INSERT_STMT;
            PreparedStatement statement = connection.prepareStatement(sqlString);
            String pathName = System.getProperty("user.dir") + "/src/main/res/cities.txt";
            Scanner cities = new Scanner(new File(pathName));
            for (int i = 0; i < DatabaseCity.CITY_COUNT * 3; i += 3) {
                String city = cities.nextLine();
                String[] vars = city.split(",");

                statement.setString(i + 1, vars[0]); //setting city name
                statement.setInt(i + 2, Integer.parseInt(vars[1])); //point x
                statement.setInt(i + 3, Integer.parseInt(vars[2])); //point y
            }
            statement.execute();
        } catch (SQLException ex) {
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeRoutes() {
        try(Connection connection = session.getConnection()) {
            String sqlString = DatabaseRoute.INSERT_STMT;
            PreparedStatement statement = connection.prepareStatement(sqlString);

            String pathName = System.getProperty("user.dir") + "/src/main/res/routes.txt";
            Scanner routes = new Scanner(new File(pathName));
            for (int i = 0; i < DatabaseRoute.ROUTE_COUNT * 5; i += 5) {
                String route = routes.nextLine();
                String[] vars = route.split(",");

                statement.setInt(i + 1, i + 1); //routeID
                statement.setString(i + 2, vars[0]); //city1
                statement.setString(i + 3, vars[1]); //city2
                statement.setString(i + 4, vars[2]); //color
                statement.setInt(i + 5, Integer.parseInt(vars[3])); //points
            }
            statement.execute();
        } catch (SQLException ex) {
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public DatabaseTrainCard getRandomTrainCard(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT * FROM (SELECT * FROM %1$s\n" +
                            "              WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)\n" +
                            "              AND %6$s IS NULL\n" +
                            "              AND %7$s = false\n" +
                            ") as newTable ORDER BY random() LIMIT 1",
                    DatabaseTrainCard.TABLE_NAME,
                    DatabaseTrainCard.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,
                    DatabaseTrainCard.PLAYER_ID,
                    DatabaseTrainCard.DISCARDED);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            ResultSet resultSet = statement.executeQuery();

            DatabaseTrainCard card = null;
            if(resultSet.next()){
                card = DatabaseTrainCard.buildTrainCardFromResultSet(resultSet);
            }
            else if(reshuffleTrainCardDiscardPile(game_name)) {
                resultSet = statement.executeQuery();
                card = DatabaseTrainCard.buildTrainCardFromResultSet(resultSet);
            }
            return card;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public DatabaseTrainCard drawRandomTrainCard(String game_name, String player_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %6$s = (SELECT %8$s FROM %9$s WHERE %10$s = ?)" +
                            "WHERE %11$s = (" +
                            "               SELECT %11$s FROM (SELECT %11$s FROM %1$s\n" +
                            "              WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)\n" +
                            "              AND %6$s IS NULL\n" +
                            "              AND %7$s = false\n" +
                            ") as newTable ORDER BY random() LIMIT 1)\n" +
                            "RETURNING *",
                    DatabaseTrainCard.TABLE_NAME,
                    DatabaseTrainCard.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,

                    DatabaseGame.GAME_NAME,
                    DatabaseTrainCard.PLAYER_ID,
                    DatabaseTrainCard.DISCARDED,
                    DatabasePlayer.ID,

                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseTrainCard.ID);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, player_name);
            statement.setString(2, game_name);
            ResultSet resultSet = statement.executeQuery();

            DatabaseTrainCard card = null;
            if(resultSet.next()){
                card = DatabaseTrainCard.buildTrainCardFromResultSet(resultSet);
            }
            else if(reshuffleTrainCardDiscardPile(game_name)) {
                resultSet = statement.executeQuery();
                card = DatabaseTrainCard.buildTrainCardFromResultSet(resultSet);
            }
            return card;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public DatabaseDestinationCard drawRandomDestinationCard(String game_name, String player_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %8$s = ?,\n" +
                            "%6$s = (SELECT %9$s FROM %10$s WHERE %11$s = ?)\n" +
                            "WHERE %12$s = (SELECT %12$s FROM %1$s\n" +
                            "WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)\n" +
                            "AND %6$s IS NULL\n" +
                            "AND %7$s = ?\n" +
                            "ORDER BY random() LIMIT 1) RETURNING *;",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,

                    DatabaseGame.GAME_NAME,
                    DatabaseDestinationCard.PLAYER_ID,
                    DatabaseDestinationCard.DISCARDED,
                    DatabaseDestinationCard.DRAWN,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseDestinationCard.ID);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, true);
            statement.setString(2, player_name);
            statement.setString(3, game_name);
            statement.setBoolean(4, false);
            ResultSet resultSet = statement.executeQuery();

            DatabaseDestinationCard card = null;
            if(resultSet.next()) {
                card = DatabaseDestinationCard.buildDestinationCardFromResultSet(resultSet);
            }
            else if(reshuffleDestinationCardDiscardPile(game_name)) {
                resultSet = statement.executeQuery();
                card = DatabaseDestinationCard.buildDestinationCardFromResultSet(resultSet);
            }
            return card;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean assignTrainCardToPlayer(DatabaseTrainCard train_card, String player_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %2$s = (" +
                            "SELECT %3$s FROM %4$s WHERE %5$s = ?)" +
                            "WHERE %6$s = ?;",
                    DatabaseTrainCard.TABLE_NAME,
                    DatabaseTrainCard.PLAYER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseTrainCard.ID);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, player_name);
            statement.setString(2, train_card.getGameID());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean reshuffleTrainCardDiscardPile(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %2$s = false" +
                            "WHERE %3$s = (SELECT %4$s FROM %5$s WHERE %6$s = ?)",
                    DatabaseTrainCard.TABLE_NAME,
                    DatabaseTrainCard.DISCARDED,
                    DatabaseTrainCard.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            return (statement.executeUpdate() > 0);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean reshuffleDestinationCardDiscardPile(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %2$s = false" +
                    "WHERE %3$s = (SELECT %4$s FROM %5$s WHERE %6$s = ?)",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.DISCARDED,
                    DatabaseDestinationCard.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            return (statement.executeUpdate() > 0);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean addCommand(BaseCommand command, String game_name, String player_name, String command_type,
                              boolean visibleToSelf, boolean visibleToAll) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s,%3$s,%4$s,%5$s,%6$s,%7$s) VALUES (\n" +
                            "(SELECT %8$s FROM %9$s WHERE %10$s = ?),\n" +
                            "(SELECT %11$s FROM %12$s WHERE %13$s = ?),\n" +
                            " ?, ?, ?, ?);",
                    DatabaseCommand.TABLE_NAME,
                    DatabaseCommand.GAME_ID,
                    DatabaseCommand.PLAYER_ID,
                    DatabaseCommand.COMMAND_TYPE,
                    DatabaseCommand.METADATA,
                    DatabaseCommand.VISIBLE_TO_SELF,
                    DatabaseCommand.VISIBLE_TO_ALL,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setString(2, player_name);
            statement.setString(3, command_type);
            statement.setString(4, Serializer.serialize(command));
            statement.setBoolean(5, visibleToSelf);
            statement.setBoolean(6, visibleToAll);

            return (statement.executeUpdate() != 0);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<BaseCommand> getCommandsSinceLastCommand(String game_name, String player_name, int lastCommandID) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT * FROM %1$s" +
                    "WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)" +
                    "AND ((%6$s = (SELECT %7$s FROM %8$s WHERE %9$s = ?)" +
                            "AND %10$s = ?)" +
                        "OR %11$s = ?)" +
                    "AND %12$s > ?;",
                    DatabaseCommand.TABLE_NAME,

                    DatabaseCommand.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,

                    DatabaseCommand.PLAYER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,

                    DatabaseCommand.VISIBLE_TO_SELF,
                    DatabaseCommand.VISIBLE_TO_ALL,
                    DatabaseCommand.COMMAND_NUMBER
                    );

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setString(2, player_name);
            statement.setBoolean(3, true);
            statement.setBoolean(4, true);
            statement.setInt(5, lastCommandID);

            ResultSet resultSet = statement.executeQuery();
            return getCommandsFromResultSet(resultSet);
        } catch(SQLException ex) {
        ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<BaseCommand> getCommandsFromResultSet(ResultSet resultSet) throws SQLException {
        List<BaseCommand> commands = new ArrayList<>();

        while(resultSet.next()) {
            commands.add(DatabaseCommand.buildCommandFromResultSet(resultSet));
        }

        return commands;
    }

    private int getCurrentPlayerTurn(String game_name) {
        try (Connection connection = session.getConnection()) {
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return 1;
    }

    public boolean claimRoute(String game_name, String username, int route_number) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s\n" +
                    "SET %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)\n" +
                    "WHERE %6$s = (SELECT %7$s FROM %8$s WHERE %9$s = ?)\n" +
                    "AND %10$s = ?",
                    DatabaseClaimedRoute.TABLE_NAME,

                    DatabaseClaimedRoute.PLAYER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,

                    DatabaseClaimedRoute.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,

                    DatabaseClaimedRoute.ROUTE_ID);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, username);
            statement.setString(2, game_name);
            statement.setInt(3, route_number);

            return (statement.executeUpdate() == 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public DatabaseCity getCity(int cityID) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT * FROM %1$s WHERE %2$s = ?",
                    DatabaseCity.TABLE_NAME,
                    DatabaseCity.ID);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setInt(1, cityID);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String id = resultSet.getString(DatabaseCity.ID);
                String name = resultSet.getString(DatabaseCity.NAME);
                int x_coord = resultSet.getInt(DatabaseCity.POINT_X);
                int y_coord = resultSet.getInt(DatabaseCity.POINT_Y);

                return new DatabaseCity(id, name, x_coord, y_coord);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean hasDestinationCards(String gameName, String playerName) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s\n" +
                            "WHERE %3$s = (SELECT %4$s FROM %5$s WHERE %6$s = ?)\n" +
                            "AND %7$s = (SELECT %8$s FROM %9$s WHERE %10$s = ?);\n",
                    DatabaseDestinationCard.ID,
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.PLAYER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseDestinationCard.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, playerName);
            statement.setString(2, gameName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
