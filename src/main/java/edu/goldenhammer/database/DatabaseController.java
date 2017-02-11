package edu.goldenhammer.database;



import edu.goldenhammer.data_types.ServerGameListItem;
import edu.goldenhammer.model.Game;
import edu.goldenhammer.model.GameList;

import edu.goldenhammer.data_types.IServerPlayer;
import edu.goldenhammer.data_types.IServerGame;
import edu.goldenhammer.data_types.ServerPlayer;
import edu.goldenhammer.data_types.ServerGame;

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
        createTable(ServerPlayer.CREATE_STMT);
        createTable(ServerGame.CREATE_STMT);
        createTable(ServerGameListItem.CREATE_STMT);
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
    public IServerPlayer getPlayerInfo(String player_user_name) {
        try (Connection connection = session.getConnection()){
            String sqlString = String.format("SELECT %1$s FROM %2$s where %3$s=?", ServerPlayer.columnNames(), ServerPlayer.TABLE_NAME, ServerPlayer.USERNAME);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, player_user_name);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                return new ServerPlayer(
                        resultSet.getString(ServerPlayer.ID),
                        resultSet.getString(ServerPlayer.USERNAME),
                        resultSet.getString(ServerPlayer.PASSWORD),
                        resultSet.getString(ServerPlayer.ACCESS_TOKEN)
                );
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private GameList getGameListFromResultSet (ResultSet resultSet) throws SQLException{
        GameList gameList = new GameList();
        Game game = null;
        while(resultSet.next()){
            String user_id = resultSet.getString((ServerGameListItem.USER_ID));
            String game_id = resultSet.getString(ServerGameListItem.GAME_ID);
            if(game == null || !game_id.equals(game.getID())){

                String name = resultSet.getString("name");
                game = new Game(game_id, name, new ArrayList<>());
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
                    ServerGameListItem.columnNames() + ", name",
                    ServerGameListItem.TABLE_NAME,
                    ServerGameListItem.USER_ID);
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
                    ServerGameListItem.columnNames() + ", name",
                    ServerGameListItem.TABLE_NAME,
                    ServerGameListItem.USER_ID,
                    ServerPlayer.ID,
                    ServerPlayer.TABLE_NAME,
                    ServerPlayer.USERNAME);
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
                    ServerPlayer.columnNames(),
                    ServerPlayer.TABLE_NAME,
                    ServerPlayer.USERNAME,
                    ServerPlayer.PASSWORD);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,username);
            statement.setString(2,password);
            ResultSet resultSet = statement.executeQuery();

            return !resultSet.next(); //if there is one result for the username and password input, it is a valid login
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param username
     * @param password
     * @return if the new user was created
     */
    @Override
    public Boolean createUser(String username, String password) {
        return null;
    }

    /**
     *
     * @param name
     * @return if a game with that name was created
     */
    @Override
    public Boolean createGame(String name) {
        return null;
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
    public List<IServerPlayer> getPlayers(String game_name) {
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
     * @param player
     * @param gameID
     * @return
     */
    @Override
    public IServerGame playGame(String player, String gameID) {
        return null;
    }

    @Override
    public void setAccessToken(String userID, String accessToken){

    }
}
