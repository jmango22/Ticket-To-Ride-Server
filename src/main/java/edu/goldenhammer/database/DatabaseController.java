package edu.goldenhammer.database;

import edu.goldenhammer.data_types.ServerGameListItem;
import edu.goldenhammer.model.Game;
import edu.goldenhammer.model.GameList;
import edu.goldenhammer.data_types.IServerPlayer;
import edu.goldenhammer.data_types.ServerPlayer;
import edu.goldenhammer.data_types.ServerGame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public GameList getGames() {
        return null;
    }

    @Override
    public GameList getGames(String player_user_name) {

        try (Connection connection = session.getConnection()) {
            String sqlString = String.format("SELECT %1$s FROM %2$s NATURAL JOIN game where %3$s=? order by game_id",
                    ServerGameListItem.columnNames() + ", name",
                    ServerGameListItem.TABLE_NAME,
                    ServerGameListItem.USER_ID);
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1,player_user_name);
            GameList gameList = new GameList();
            ResultSet resultSet = statement.executeQuery();
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
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean login(String username, String password) {
        return null;
    }

    @Override
    public Boolean createUser(String username, String password) {
        return null;
    }

    @Override
    public Boolean createGame(String name) {
        return null;
    }

    @Override
    public Boolean joinGame(String player, String gameID) {
        return null;
    }

    @Override
    public List<IServerPlayer> getPlayers(String gameID) {
        return null;
    }

    @Override
    public void setAccessToken(String userID, String accessToken){

    }
}
