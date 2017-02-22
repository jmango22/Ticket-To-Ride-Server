package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/22/2017.
 */
public class DatabaseClaimedRoute implements IDatabaseClaimedRoute{
    public static final String TABLE_NAME = "claimed_route";
    public static final String ROUTE_ID = "route_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s INTEGER NOT NULL," +
                    "%3$s INTEGER NOT NULL," +
                    "%4$s INTEGER NOT NULL," +
                    "PRIMARY KEY(%2$s, %3$s, %4$s)," +
                    "FOREIGN KEY(%2$s)" +
                    "   REFERENCES %5$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%3$s)" +
                    "   REFERENCES %6$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%4$s)" +
                    "   REFERENCES %7$s" +
                    "   ON DELETE CASCADE" +
                    ");",
            TABLE_NAME,
            ROUTE_ID,
            GAME_ID,
            PLAYER_ID,
            DatabaseRoute.TABLE_NAME,
            DatabaseGame.TABLE_NAME,
            DatabasePlayer.TABLE_NAME
    );


    public DatabaseClaimedRoute(String routeID, String gameID, String playerID) {
        this.routeID = routeID;
        this.gameID = gameID;
        this.playerID = playerID;
    }

    @Override
    public String getRouteID() {
        return routeID;
    }

    @Override
    public String getGameID() {
        return gameID;
    }

    @Override
    public String getPlayerID() {
        return playerID;
    }

    private String routeID;
    private String gameID;
    private String playerID;
}
