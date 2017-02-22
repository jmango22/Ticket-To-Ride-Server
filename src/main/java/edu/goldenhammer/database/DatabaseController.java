package edu.goldenhammer.database;



import edu.goldenhammer.database.data_types.DatabaseParticipants;
import edu.goldenhammer.model.GameListItem;
import edu.goldenhammer.model.GameList;

import edu.goldenhammer.database.data_types.IDatabasePlayer;
import edu.goldenhammer.database.data_types.DatabasePlayer;
import edu.goldenhammer.database.data_types.DatabaseGame;
import edu.goldenhammer.model.GameModel;
import edu.goldenhammer.model.IGameModel;

import java.util.List;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.ArrayList;


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
     * @return
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
     * @return
     */
    public DatabaseController(){
        initializeDatabase();
    }

    private void initializeDatabase() {
        this.session = DatabaseConnectionFactory.getInstance();
        ensureTablesCreated();
    }

    private void ensureTablesCreated() {
        createTable(DatabasePlayer.CREATE_STMT);
        createTable(DatabaseGame.CREATE_STMT);
        createTable(DatabaseParticipants.CREATE_STMT);
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
     * @post The IDatabasePlayer associated with the username is returned
     * @return
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
            String sqlString = String.format("SELECT %1$s, %2$s, %3$s FROM %4$s NATURAL JOIN %5$s" +
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
            String sqlString = String.format("SELECT %1$s, %2$s, %3$s FROM %4$s NATURAL JOIN %5$s" +
                            "NATURAL JOIN %6$s WHERE %7$s IN (SELECT %8$s FROM %9$s" +
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
            e.printStackTrace();
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
            String sqlString = String.format(" INSERT INTO %1$s (%2$s, %3$s) " +
                            "(SELECT %4$s.%5$s, %6$s.%7$s FROM %8$s, %9$s " +
                            "WHERE %10$s.%11$s = ? AND %12$s.%13$s = ? AND " +
                            "5 > (SELECT count(*) FROM %14$s WHERE %15$s IN " +
                            "(SELECT %16$s FROM %17$s WHERE %18$s = ?)) );",
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.USER_ID,
                    DatabaseParticipants.GAME_ID,

                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabasePlayer.TABLE_NAME,

                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.TABLE_NAME,

                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.GAME_ID,

                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME
                    );
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,player_name);
            statement.setString(2,game_name);
            statement.setString(3,game_name);
            statement.execute();
            return true;
            //TODO: make this function return if it was entered not if it executed
        } catch (SQLException e) {
            e.printStackTrace();
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
     * @return the list of players that are a member of the game
     */
    @Override
    public List<String> getPlayers(String game_name) {
        try (Connection connection = session.getConnection()) {

            //get the information to make the GameModel object from the database
            String sqlString = String.format("SELECT %1$s,%2$s FROM %3$s NATURAL JOIN %4$s" +
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
            statement.execute();
            return true;
            //TODO: make this function return if it was entered not if it executed
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
            //update the database to indicate the game has started
            String sqlString = String.format("UPDATE %1$s SET %2$s = ? WHERE %3$s = ?",
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.STARTED,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, true);
            statement.setString(2, game_name);
            statement.executeUpdate();

            //get the information to make the GameModel object from the database
            sqlString = String.format("SELECT %1$s, %2$s FROM %3$s NATURAL JOIN %4$s WHERE %5$s IN" +
                            "(SELECT %6$s FROM %7$s WHERE %8$s = ?)",
                    DatabaseGame.columnNames(),
                    DatabaseParticipants.PLAYER_NUMBER,
                    DatabaseGame.TABLE_NAME,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.GAME_ID,
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME);
            statement = connection.prepareStatement(sqlString);
            statement.setString(1,game_name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new GameModel(resultSet.getString(DatabaseGame.ID),
                        resultSet.getString(DatabaseGame.GAME_NAME),
                        resultSet.getBoolean(DatabaseGame.STARTED),
                        getPlayers(game_name));
            }
        } catch(SQLException e){
            e.printStackTrace();
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
}
