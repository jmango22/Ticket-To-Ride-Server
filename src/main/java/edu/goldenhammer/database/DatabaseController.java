package edu.goldenhammer.database;

import edu.goldenhammer.database.data_types.*;
import edu.goldenhammer.model.*;
import edu.goldenhammer.server.Serializer;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.commands.EndTurnCommand;
import edu.goldenhammer.server.commands.InitializeHandCommand;

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
            boolean started = resultSet.getBoolean(DatabaseGame.STARTED);
            if(game == null || !game_id.equals(game.getID())){

                String name = resultSet.getString(DatabaseGame.GAME_NAME);
                game = new GameListItem(game_id, name, started, new ArrayList<>());
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
            String sqlString = String.format("SELECT %1$s, %2$s, %3$s, %7$s FROM %4$s NATURAL JOIN %5$s " +
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
            String sqlString = String.format("SELECT %1$s, %2$s, %3$s, %15$s FROM %4$s NATURAL JOIN %5$s\n" +
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
                    DatabaseGame.ID,
                    DatabaseGame.STARTED);
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
        synchronized(Lock.getInstance().getLock(game_name)) {
            try (Connection connection = session.getConnection()) {
                //get the information to make the GameModel object from the database
                String sqlString = String.format("SELECT %1$s FROM %2$s WHERE %3$s = ?",
                        DatabaseGame.STARTED,
                        DatabaseGame.TABLE_NAME,
                        DatabaseGame.GAME_NAME);
                PreparedStatement statement = connection.prepareStatement(sqlString);
                statement.setString(1, game_name);
                ResultSet resultSet = statement.executeQuery();

                List<String> players = getPlayers(game_name);
                if (resultSet.next() && players.size() > 1) {
                    if (!resultSet.getBoolean(DatabaseGame.STARTED)) {
                        initializeGame(game_name);
                    }
                    IGameModel gameModel = getGameModel(game_name);
                    return gameModel;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
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

    public int getDestinationCardCount(String player_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT count(*) FROM %1$s WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.PLAYER_ID,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, player_name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
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

    public List<Track> getTracks(String game_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format(
                    "with tracks as (SELECT %1$s.%3$s,\n" +
                            "city1.%8$s AS city1_point_x, city1.%9$s AS city1_point_y, city1.%10$s AS city1_name,\n" +
                            "%1$s.%4$s,\n" +
                            "city2.%8$s AS city2_point_x, city2.%9$s AS city2_point_y, city2.%10$s AS city2_name,\n" +
                            "%1$s.%5$s, %1$s.%22$s, route_player_numbers.%18$s, route.route_number FROM %1$s\n" +
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
                            "ON route_player_numbers.%14$s = %1$s.%2$s)" +
                        "select *, (select (count(*)=1) as second from tracks t2 where t1.route_number > t2.route_number and t1.city_1=t2.city_1 and t1.city_2=t2.city_2)\n" +
                        "from tracks t1;",
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
                int route_number = resultSet.getInt("route_number");
                int length = resultSet.getInt(DatabaseRoute.ROUTE_LENGTH);
                boolean second = resultSet.getBoolean("second");
                Color color = Color.getTrackColorFromString(resultSet.getString(DatabaseRoute.ROUTE_COLOR));
                int owner = resultSet.getInt(DatabaseParticipants.PLAYER_NUMBER);
                if(resultSet.wasNull()) {
                    owner = -1;
                }

                tracks.add(new Track(city1, city2, length, color, owner, location1x, location1y, location2x, location2y, route_number, second));
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
        initializeHands(game_name);
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

    private void initializeHands(String game_name) {
        List<String> players = getPlayers(game_name);
        int commandNumber = 0;
        for(String player : players) {
            BaseCommand command = new InitializeHandCommand();
            command.setGameName(game_name);
            command.setCommandNumber(commandNumber);
            commandNumber++;
            command.setPlayerName(player);
            command.execute();
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
            initializeSlots(game_name);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeSlots(String game_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("WITH random_cards AS (\n" +
                    "SELECT train_card_id FROM train_card\n" +
                    "WHERE game_id IN (SELECT game_id FROM game WHERE name = ?)\n" +
                    "AND discarded = false\n" +
                    "AND player_id IS NULL\n" +
                    "ORDER BY random()\n" +
                    "LIMIT 5),\n" +
                    "card_and_slots AS (SELECT row_number() over() as slot, * FROM random_cards)\n" +
                    "UPDATE train_card SET slot = cards_and_slots.slot - 1\n" +
                    "FROM cards_and_slots\n" +
                    "WHERE game_id IN (SELECT game_id FROM game WHERE name=?)\n" +
                    "AND train_card.train_card_id = cards_and_slots.train_card_id;\n");
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setString(2, game_name);
            statement.execute();
        } catch(SQLException ex) {
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

    public DatabaseTrainCard drawRandomTrainCard(String game_name, String player_name) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %6$s = (SELECT %8$s FROM %9$s WHERE %10$s = ?)" +
                            "WHERE %2$s in (SELECT %3$s FROM %4$s WHERE %5$s = ?)\n" +
                            "and %11$s = (" +
                            "               SELECT %11$s FROM (SELECT %11$s FROM %1$s\n" +
                            "              WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)\n" +
                            "              AND %6$s IS NULL\n" +
                            "              AND %7$s = false\n" +
                            ") " +
                            "as newTable ORDER BY random() LIMIT 1)\n" +
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
            statement.setString(3, game_name);
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

    @Override
    public DatabaseTrainCard drawTrainCardFromSlot(String game_name, String player_name, int slot) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("WITH selected_card AS\n" +
                        "(SELECT train_card_id FROM train_card\n" +
                        "WHERE game_id IN (SELECT game_id FROM game WHERE name = ?)\n" +
                        "AND slot = ?)\n" +
                    "UPDATE train_card SET player_id = (SELECT user_id FROM player WHERE username = ?)\n" +
                    "FROM selected_card\n" +
                    "WHERE train_card.train_card_id IN selected_card.train_card_id\n" +
                    "AND EXISTS (\n" +
                        "WITH random_card AS\n" +
                            "(SELECT train_card_id FROM train_card\n" +
                            "WHERE game_id IN (SELECT game_id FROM game WHERE name = ?)" +
                            "AND slot IS NULL" +
                            "AND player_id IS NULL" +
                            "AND discarded = false" +
                            "ORDER BY random()" +
                            "LIMIT 1)" +
                        "UPDATE train_card SET slot = ?\n" +
                        "FROM random_card\n" +
                        "WHERE train_card.train_card_id IN random_card.train_card_id\n" +
                        "RETURNING *)" +
                    ")\n" +
                    "RETURNING *;");
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setInt(2, slot);
            statement.setString(3, player_name);
            statement.setString(4, game_name);
            statement.setInt(5, slot);
            ResultSet resultSet = statement.executeQuery();

            DatabaseTrainCard card = null;
            if(resultSet.next()) {
                card = DatabaseTrainCard.buildTrainCardFromResultSet(resultSet);
            }
            else if(reshuffleTrainCardDiscardPile(game_name)) {
                resultSet = statement.executeQuery();
                resultSet.next();
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
//    SELECT * FROM commandWHERE game_id = (SELECT game_id FROM game WHERE name = ?)AND ((player_id = (SELECT user_id FROM player WHERE username = ?)AND visible_to_self = ?)OR visible_to_all = ?)AND command_number > ?;
    public List<BaseCommand> getCommandsSinceLastCommand(String game_name, String player_name, int lastCommandID) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("(SELECT * FROM %1$s natural join participants where user_id=player_id" +
                    " and %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?)" +
//                    " AND ((%6$s = (SELECT %7$s FROM %8$s WHERE %9$s = ?)" +
//                            " AND %10$s = ?)" +
//                        " OR %11$s = ?)" +
                    " AND %12$s >= ? order by command_number);",
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
//            statement.setString(2, player_name);
//            statement.setBoolean(3, true);
//            statement.setBoolean(4, true);
            statement.setInt(2, lastCommandID);

            ResultSet resultSet = statement.executeQuery();
            return getCommandsFromResultSet(resultSet, player_name);
        } catch(SQLException ex) {
        ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<BaseCommand> getCommandsFromResultSet(ResultSet resultSet, String player_name) throws SQLException {
        List<BaseCommand> commands = new ArrayList<>();

        while(resultSet.next()) {
            commands.add(DatabaseCommand.buildCommandFromResultSet(resultSet, player_name));
        }

        return commands;
    }

    public int getCurrentPlayerTurn(String game_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s\n" +
                            "WHERE %3$s IN (SELECT %4$s FROM %5$s WHERE %6$s = ?)\n" +
                            "AND %7$s IN (SELECT %8$s FROM %9$s\n" +
                                "WHERE %10$s IN (SELECT %4$s FROM %5$s WHERE %6$s = ?)\n" +
                                "AND %11$s = ?\n" +
                                "ORDER BY %12$s DESC\n" +
                                "LIMIT 1);\n",
                    DatabaseParticipants.PLAYER_NUMBER,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,
                    DatabaseParticipants.USER_ID,
                    DatabaseCommand.PLAYER_ID,
                    DatabaseCommand.TABLE_NAME,
                    DatabaseCommand.GAME_ID,
                    DatabaseCommand.COMMAND_TYPE,
                    DatabaseCommand.COMMAND_NUMBER);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setString(2, game_name);
            statement.setString(3, "EndTurn");

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt(DatabaseParticipants.PLAYER_NUMBER);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
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

    private boolean addFirstCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll) {
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%14$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s) VALUES (\n" +
                            "   ?,\n" +
                            "   (SELECT %8$s FROM %9$s WHERE %10$s = ?" +
                            "       AND NOT EXISTS (SELECT * FROM %1$s WHERE %14$s = ?\n" +
                            "           AND %2$s = (SELECT %8$s FROM %9$s WHERE %10$s = ?))),\n" +
                            "   (SELECT %11$s FROM %12$s WHERE %13$s = ?),\n" +
                            "   ?,\n ?,\n ?,\n ?);",
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
                    DatabasePlayer.USERNAME,

                    DatabaseCommand.COMMAND_NUMBER);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setInt(1, cmd.getCommandNumber());
            statement.setString(2, cmd.getGameName());
            statement.setInt(3, cmd.getCommandNumber());
            statement.setString(4, cmd.getGameName());
            statement.setString(5, cmd.getPlayerName());
            statement.setString(6, cmd.getName());
            statement.setString(7, Serializer.serialize(cmd));
            statement.setBoolean(8, visibleToSelf);
            statement.setBoolean(9, visibleToAll);

            return statement.executeUpdate() == 1;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public synchronized boolean addCommand(BaseCommand cmd, boolean visibleToSelf, boolean visibleToAll) {
        if(cmd.getCommandNumber() == 0) {
            return addFirstCommand(cmd, visibleToSelf, visibleToAll);
        }
        try(Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%14$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s) VALUES (\n" +
                            "   ?,\n" +
                            "   (SELECT %8$s FROM %9$s WHERE %10$s = ?" +
                            "       AND EXISTS (SELECT * FROM %1$s WHERE %14$s = ?\n" +
                            "           AND %2$s = (SELECT %8$s FROM %9$s WHERE %10$s = ?))),\n" +
                            "   (SELECT %11$s FROM %12$s WHERE %13$s = ?),\n" +
                            "   ?,\n ?,\n ?,\n ?);",
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
                    DatabasePlayer.USERNAME,

                    DatabaseCommand.COMMAND_NUMBER);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setInt(1, cmd.getCommandNumber());
            statement.setString(2, cmd.getGameName());
            statement.setInt(3, cmd.getCommandNumber() - 1);
            statement.setString(4, cmd.getGameName());
            statement.setString(5, cmd.getPlayerName());
            statement.setString(6, cmd.getName());
            statement.setString(7, Serializer.serialize(cmd));
            statement.setBoolean(8, visibleToSelf);
            statement.setBoolean(9, visibleToAll);

            return statement.executeUpdate() == 1;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean returnDestCards(String gameName, String playerName, List<DestinationCard> destinationCards) {
        if(destinationCards.size() == 1 && getDrawnDestCardCount(gameName, playerName) == 3) {
            return returnSingleDestCard(gameName, playerName, destinationCards.get(0));
        }
        else if(destinationCards.size() == 2
                && getDrawnDestCardCount(gameName, playerName) == 3
                && getDestinationCardCount(playerName) > 3){
            try (Connection connection = session.getConnection()) {
                String sqlString = String.format("UPDATE %1$s SET %2$s = null, %3$s = ?, %4$s = ?\n" +
                        "WHERE %2$s = (SELECT %5$s FROM %6$s WHERE %7$s = ?)\n" +
                        "AND %8$s = (SELECT %9$s FROM %10$s WHERE %11$s = ?)\n" +
                        "AND ((%12$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?)\n" +
                        "   AND %13$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?))\n" +
                        "OR (%12$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?)\n" +
                        "   AND %13$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?)))\n" +
                        "AND %3$s = ?;\n" +
                        "UPDATE %1$s SET %3$s = ?\n" +
                                "WHERE %2$s = (SELECT %5$s FROM %6$s WHERE %7$s = ?)\n" +
                                "AND %8$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?);\n",
                        DatabaseDestinationCard.TABLE_NAME,
                        DatabaseDestinationCard.PLAYER_ID,
                        DatabaseDestinationCard.DRAWN,
                        DatabaseDestinationCard.DISCARDED,

                        DatabasePlayer.ID, //5
                        DatabasePlayer.TABLE_NAME,
                        DatabasePlayer.USERNAME,

                        DatabaseDestinationCard.GAME_ID, //8

                        DatabaseGame.ID, //9
                        DatabaseGame.TABLE_NAME,
                        DatabaseGame.GAME_NAME,

                        DatabaseDestinationCard.CITY_1, //12
                        DatabaseDestinationCard.CITY_2,

                        DatabaseCity.ID, //14
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME);
                PreparedStatement statement = connection.prepareStatement(sqlString);
                statement.setBoolean(1, false);
                statement.setBoolean(2, true);
                statement.setString(3, playerName);
                statement.setString(4, gameName);
                statement.setString(5, destinationCards.get(0).getCity1().getName());
                statement.setString(6, destinationCards.get(0).getCity2().getName());
                statement.setString(7, destinationCards.get(1).getCity1().getName());
                statement.setString(8, destinationCards.get(1).getCity2().getName());
                statement.setBoolean(9, true);
                statement.setBoolean(10, false);
                statement.setString(11, playerName);
                statement.setString(12, gameName);

                return statement.executeUpdate() == 2;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private boolean returnSingleDestCard(String gameName, String playerName, DestinationCard destinationCard) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("UPDATE %1$s SET %2$s = null, %3$s = ?, %4$s = ?\n" +
                    "WHERE %5$s = (SELECT %6$s FROM %7$s WHERE %8$s = ?)\n" +
                    "AND %2$s = (SELECT %9$s FROM %10$s WHERE %11$s = ?)\n" +
                    "AND %12$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?)" +
                    "AND %13$s = (SELECT %14$s FROM %15$s WHERE %16$s = ?)" +
                    "AND %3$s = ?;" +
                            "UPDATE %1$s SET %3$s = ?\n" +
                            "WHERE %5$s = (SELECT %6$s FROM %7$s WHERE %8$s = ?)\n" +
                            "AND %2$s = (SELECT %9$s FROM %10$s WHERE %11$s = ?);\n",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.PLAYER_ID,
                    DatabaseDestinationCard.DRAWN,
                    DatabaseDestinationCard.DISCARDED,
                    DatabaseDestinationCard.GAME_ID,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,

                    DatabaseDestinationCard.CITY_1,
                    DatabaseDestinationCard.CITY_2,

                    DatabaseCity.ID,
                    DatabaseCity.TABLE_NAME,
                    DatabaseCity.NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, false);
            statement.setBoolean(2, true);
            statement.setString(3, gameName);
            statement.setString(4, playerName);
            statement.setString(5, destinationCard.getCity1().getName());
            statement.setString(6, destinationCard.getCity2().getName());
            statement.setBoolean(7, true);
            statement.setBoolean(8, false);
            statement.setString(9, gameName);
            statement.setString(10, playerName);

            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private int getDrawnDestCardCount(String gameName, String playerName) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT * FROM %1$s\n" +
                    "WHERE %2$s = (SELECT %6$s FROM %7$s WHERE %8$s = ?)\n" +
                    "AND %3$s = (SELECT %9$s FROM %10$s WHERE %11$s = ?)\n" +
                    "AND %4$s = ?" +
                    "AND %5$s = ?",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.PLAYER_ID,
                    DatabaseDestinationCard.GAME_ID,
                    DatabaseDestinationCard.DISCARDED,
                    DatabaseDestinationCard.DRAWN,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, playerName);
            statement.setString(2, gameName);
            statement.setBoolean(3, false);
            statement.setBoolean(4, true);

            ResultSet resultSet = statement.executeQuery();

            int drawnCardCount = 0;
            while(resultSet.next()) {
                ++drawnCardCount;
            }
            return drawnCardCount;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public List<DatabaseDestinationCard> getPlayerDestinationCards(String game_name, int player_id) {
        List<DatabaseDestinationCard> playerDestCards = new ArrayList<>();
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT *" +
                            "FROM %1$s" +
                            "WHERE (%2$s = (SELECT %3$s FROM %4$s WHERE %5s = ?) AND %6$s = ? AND %7$s = ? AND %8$s = ?)",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.GAME_ID,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,

                    DatabaseDestinationCard.PLAYER_ID,
                    DatabaseDestinationCard.DRAWN,
                    DatabaseDestinationCard.DISCARDED);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setString(2, Integer.toString(player_id));
            statement.setString(3, "TRUE");
            statement.setString(4, "FALSE");

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                playerDestCards.add(DatabaseDestinationCard.buildDestinationCardFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return playerDestCards;
    }

    @Override
    public boolean postMessage(String game_name, String player_name, String message) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s, %3$s, %4$s)" +
                            "VALUES ((SELECT %5$s FROM %6$s WHERE %7$s = ?)," +
                            "(SELECT %8$s FROM %9$s WHERE %10$s = ?), ?)",
                    DatabaseMessage.TABLE_NAME,
                    DatabaseMessage.GAME_ID,
                    DatabaseMessage.PLAYER_ID,
                    DatabaseMessage.MESSAGE,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);
            statement.setString(2, player_name);
            statement.setString(3, message);

            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DatabaseMessage> getMessages(String game_name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT * FROM %1$s\n" +
                            "WHERE %2$s = (SELECT %3$s FROM %4$s WHERE %5$s = ?);",
                    DatabaseMessage.TABLE_NAME,
                    DatabaseMessage.GAME_ID,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, game_name);

            ResultSet resultSet = statement.executeQuery();

            List<DatabaseMessage> messages = new ArrayList<>();
            while(resultSet.next()) {
                messages.add(DatabaseMessage.parseResultSetRow(resultSet));
            }

            return messages;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String getUsername(String player_id) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s\n" +
                            "WHERE %3$s = ?;",
                    DatabasePlayer.USERNAME,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.ID);

            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setInt(1, Integer.parseInt(player_id));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString(DatabasePlayer.USERNAME);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DatabaseDestinationCard> drawDestinationCards(String game_name, String username, int commandNumber, int player_number) {
        List<DatabaseDestinationCard> cards = getDestinationCards(game_name, username, commandNumber, player_number);
        if(cards != null && cards.size() < 3) {
            reshuffleDestinationCardDiscardPile(game_name);
            cards = getDestinationCards(game_name, username, commandNumber, player_number);
        }
        return cards;
    }

    private List<DatabaseDestinationCard> getDestinationCards(String game_name, String username, int commandNumber, int player_number) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format(
                    "UPDATE %1$s SET %2$s = ?,\n" +
                            "%3$s = (SELECT %7$s FROM %8$s WHERE %9$s = ?),\n" + //player id
                            "WHERE %4$s = (SELECT %10$s FROM %11$s WHERE %12$s = ?)\n" + //game id
                            "AND %3$s IS NULL\n" + //player id is null
                            "AND %5$s = ?\n" + //not discarded
                            "AND EXISTS (SELECT * FROM (\n" + //make sure there are three destination cards left in the deck
                            "       SELECT count(*) FROM %1$s\n" +
                            "       WHERE %4$s = (SELECT %10$s FROM %11$s WHERE %12$s = ?)\n" +
                            "       AND %3$s IS NULL\n" +
                            "       AND %5$s = ?) AS dest_card_count WHERE count >= ?)\n" +
                            "AND %6$s IN (\n" + //select three random destination card ids to update
                            "   SELECT %6$s FROM %1$s\n" +
                            "   WHERE %4$s = (SELECT %10$s FROM %11$s WHERE %12$s = ?)\n" +
                            "   AND %3$s IS NULL\n" +
                            "   ORDER BY random()\n" +
                            "   LIMIT 3)\n" +
                            "AND NOT EXISTS (" + //make sure the command number has not been used yet
                            "SELECT * FROM commands\n" +
                            "WHERE game_id IN (SELECT game_id FROM game WHERE game_name = ?)\n" +
                            "AND command_number = ?)\n" +
                            "AND EXISTS (\n" + //make sure the last command was the previous player's end turn command
                            "SELECT * FROM commands\n" +
                            "WHERE game_id IN (SELECT game_id FROM game WHERE game_name = ?)\n" +
                            "AND command_number = ?\n" +
                            "AND player_number IN (\n" + //find the previous player number
                            "       SELECT player_number FROM participants\n" +
                            "       WHERE player_id = (SELECT user_id FROM player WHERE username = ?)\n" +
                            "       AND game_id = (SELECT game_id FROM game WHERE name = ?)\n" +
                            "       AND (player_number = ? - 1\n" + //player number can be either the number minus one
                            "           OR player_number = ? + (\n" +//or the total number of participants, if player zero
                            "               SELECT player_number FROM participants\n" +
                            "               WHERE game_id = (SELECT game_id FROM game WHERE name = ?)\n" +
                            "               ORDER BY player_number DESC\n" + //make sure the highest number (the total
                            "               LIMIT 1)\n" +                    //number of participants) is on top
                            ")\n" +
                            "AND command_type = ?)\n" + //command type needs to be EndTurn
                            "RETURNING *\n" +
                            ");",
                    DatabaseDestinationCard.TABLE_NAME,
                    DatabaseDestinationCard.DRAWN,
                    DatabaseDestinationCard.PLAYER_ID,
                    DatabaseDestinationCard.GAME_ID,
                    DatabaseDestinationCard.DISCARDED,
                    DatabaseDestinationCard.ID,

                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME
            );
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, true);
            statement.setString(2, username);
            statement.setString(3, game_name);
            statement.setBoolean(4, false);
            statement.setString(5, game_name);
            statement.setBoolean(6, false);
            statement.setInt(7, 3);
            statement.setString(8, game_name);
            statement.setString(9, game_name);
            statement.setInt(10, commandNumber);
            statement.setString(11, game_name);
            statement.setInt(12, commandNumber - 1);
            statement.setString(13, username);
            statement.setString(14, game_name);
            statement.setInt(15, player_number);
            statement.setInt(16, player_number);
            statement.setString(17, game_name);
            statement.setString(18, "EndTurn");

            ResultSet resultSet = statement.executeQuery();
            List<DatabaseDestinationCard> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(DatabaseDestinationCard.buildDestinationCardFromResultSet(resultSet));
            }
            return cards;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

//    with card as (select * from train_card
//            where game_id in (select game_id from game where name='aaaa')
//    and player_id in (select user_id from player where username='devon')
//    and train_type='yellow'
//    Limit 1
//    FOR UPDATE)
//    update train_card t set discarded = true, player_id = null
//    from card
//    where t.train_card_id = card.train_card_id
//    and t.game_id = card.game_id;
    @Override
    public boolean discardCard(String gameName, String playerName, Color color) {
        try (Connection connection = session.getConnection()) {
            color.toString();
            String sqlString = String.format("" +
                    "with card as (select * from train_card\n" +
                    "      where game_id in (select game_id from game where name=?)\n" +
                    "      and player_id in (select user_id from player where username=?)\n" +
                    "      and train_type=?\n" +
                    "      Limit 1\n" +
                    "      FOR UPDATE)\n" +
                    "    update train_card t set discarded = true, player_id = null\n" +
                    "      from card\n" +
                    "      where t.train_card_id = card.train_card_id\n" +
                    "      and t.game_id = card.game_id");
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, gameName);
            statement.setString(2, playerName);
            statement.setString(3, color.toString().toLowerCase());
            return statement.executeUpdate() == 1;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean allHandsInitialized(String gameName) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format(
                    "with initialize as (\n" +
                            "select count(*) as count from command where game_id = (select game_id from game where name=?) and command_type='InitializeHand'\n" +
                            "), returned as (\n" +
                            "select count(*) as count from command where game_id = (select game_id from game where name=?) and command_type='ReturnDestCards'\n" +
                            ")\n" +
                    "select (i.count<=r.count AND i.count > 0) from initialize as i, returned as r"
            );
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, gameName);
            statement.setString(2, gameName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch( Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public EndTurnCommand getEndTurnCommand(String gameName, int commandNumber, String playerName) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("with current_player as (select * from participants where \n" +
                    "\tgame_id in (select game_id from game where name=?)\n" +
                    "\tand user_id in (select user_id from player where username='devon' limit 1)\n" +
                    "), max_player as (select max(player_number) as max from participants\n" +
                    "    where game_id in (select game_id from game where name=?))\n" +
                    "select player_number as previous, mod(player_number + 1,max + 1) as next from  max_player, current_player\n");
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, gameName);
            statement.setString(2, gameName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                EndTurnCommand command = new EndTurnCommand();
                command.setPlayerName(playerName);
                command.setGameName(gameName);
                command.setCommandNumber(commandNumber);
                command.setNextPlayer(resultSet.getInt("next"));
                int previous = resultSet.getInt("previous");
                command.setPreviousPlayer(previous);
                command.setPlayerNumber(previous);
                return command;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public boolean playerHasCards(int wildCount, int nonWildCount, Color color, String gameName) {
        try (Connection connection = session.getConnection()) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
