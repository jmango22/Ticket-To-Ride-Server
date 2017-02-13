package edu.goldenhammer.database;



import com.sun.org.apache.regexp.internal.RE;
import edu.goldenhammer.database.data_types.DatabaseParticipants;
import edu.goldenhammer.model.GameOverview;
import edu.goldenhammer.model.GameList;

import edu.goldenhammer.database.data_types.IDatabasePlayer;
import edu.goldenhammer.database.data_types.IDatabaseGame;
import edu.goldenhammer.database.data_types.DatabasePlayer;
import edu.goldenhammer.database.data_types.DatabaseGame;
import edu.goldenhammer.model.Gameplay;
import edu.goldenhammer.model.IGameplay;

import javax.xml.crypto.Data;
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

    public static DatabaseController getInstance(){
        if(singleton == null)
            singleton = new DatabaseController();
        return singleton;
    }


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

    @Override
    public IDatabasePlayer getPlayerInfo(String player_user_name) {
        try (Connection connection = session.getConnection()){
            String sqlString = String.format("SELECT %1$s FROM %2$s where %3$s=?", DatabasePlayer.columnNames(), DatabasePlayer.TABLE_NAME, DatabasePlayer.USERNAME);
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
        GameOverview game = null;
        while(resultSet.next()){
            String user_id = resultSet.getString((DatabaseParticipants.USER_ID));
            String game_id = resultSet.getString(DatabaseParticipants.GAME_ID);
            if(game == null || !game_id.equals(game.getID())){

                String name = resultSet.getString("name");
                game = new GameOverview(game_id, name, false, new ArrayList<>());
                gameList.add(game);
            }
            game.getPlayers().add(user_id);
        }
        return gameList;
    }

    /**
     *
     * @return all games that have not been started
     */
    @Override
    public GameList getGames() {

        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s NATURAL JOIN game where started=false order by game_id",
                    DatabaseParticipants.columnNames() + ", name",
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.USER_ID);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            ResultSet resultSet = statement.executeQuery();
            return getGameListFromResultSet(resultSet);
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param player_user_name
     * @return all games in which the player is a participant
     */
    @Override
    public GameList getGames(String player_user_name) {

        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s NATURAL JOIN game where %3$s in (select %4$s from %5$s where %6$s=?) order by game_id",
                    DatabaseParticipants.columnNames() + ", name",
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseParticipants.USER_ID,
                    DatabasePlayer.ID,
                    DatabasePlayer.TABLE_NAME,
                    DatabasePlayer.USERNAME);
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
     *
     * @param username users username
     * @param password the password
     * @return if the player exists
     */
    @Override
    public Boolean login(String username, String password) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s where %3$s=? and %4$s=?",
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
     * @param username
     * @param password
     * @return if the new user was created
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
     * @param name
     * @return if a game with that name was created
     */
    @Override
    public Boolean createGame(String name) {
        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("INSERT INTO %1$s(%2$s,%3$b) VALUES (?,?)",
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
     * @param player_name
     * @param game_name
     * @return if the player was added to the game.
     */
    @Override
    public Boolean joinGame(String player_name, String game_name) {
        return null;
    }

    /**
     *
     * @param game_name
     * @return the list of players that are a member of the game
     */
    @Override
    public List<String> getPlayers(String game_name) {
        return null;
    }

    /**
     *
     * @param player_name
     * @param game_name
     * @return if the player left the game
     */
    @Override
    public Boolean leaveGame(String player_name, String game_name) {
        return null;
    }

    /**
     *
     * @param player_user_name
     * @param game_name
     * @return
     */
    @Override
    public IGameplay playGame(String player_user_name, String game_name) {
        try (Connection connection = session.getConnection()) {
            //update the database to indicate the game has started
            String sqlString = String.format("UPDATE %1$s SET $2$s = ? WHERE %3$s = ?",
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.STARTED,
                    DatabaseGame.GAME_NAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setBoolean(1, true);
            statement.setString(2, game_name);
            statement.executeUpdate();

            //get the information to make the Gameplay object from the database
            sqlString = String.format("SELECT %1$s FROM %2$s NATURAL JOIN %3$s WHERE %4$s IN %5$s",
                    DatabaseGame.columnNames() + DatabaseParticipants.PLAYER_NUMBER,
                    DatabaseGame.TABLE_NAME,
                    DatabaseParticipants.TABLE_NAME,
                    DatabaseGame.ID,
                    DatabaseParticipants.GAME_ID);
            statement = connection.prepareStatement(sqlString);
            ResultSet resultSet = statement.executeQuery();

            return new Gameplay(resultSet.getString(DatabaseGame.ID),
                    resultSet.getString(DatabaseGame.GAME_NAME),
                    resultSet.getBoolean(DatabaseGame.STARTED),
                    getPlayers(game_name));
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

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
}
