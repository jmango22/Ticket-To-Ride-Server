package edu.goldenhammer.model;

import edu.goldenhammer.data_types.GameList;
import edu.goldenhammer.data_types.Player;
import edu.goldenhammer.data_types.ServerPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.List;

/**
 * Created by devonkinghorn on 2/4/17.
 */
public class DatabaseController implements IDatabaseController {

    private static DatabaseController singleton;
    private DatabaseConnnectionFactory session;

    public static DatabaseController getInstance(){
        if(singleton == null)
            singleton = new DatabaseController();
        return singleton;
    }


    public DatabaseController(){
        initializeDatabase();
    }
    private void initializeDatabase() {
        this.session = DatabaseConnnectionFactory.getInstance();
        ensureTablesCreated();
    }

    private void ensureTablesCreated() {
        createTable(ServerPlayer.CREATE_STMT);
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
    public Player getPlayerInfo(String player_user_name) {
        try (Connection connection = session.getConnection()){
            String sqlString = String.format("SELECT %1$s FROM %2$s where %3$s=%4$s", ServerPlayer.columnNames(), ServerPlayer.TABLE_NAME, ServerPlayer.USERNAME, player_user_name);
            PreparedStatement statement = connection.prepareStatement(sqlString);
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
    public GameList getGames(String player) {
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
    public List<Player> getPlayers(String gameID) {
        return null;
    }

    @Override
    public void setAccessToken(String userID, String accessToken){

    }
}
