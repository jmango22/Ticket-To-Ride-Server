package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/22/2017.
 */
public class DatabaseCommand implements IDatabaseCommand{

    public static final String TABLE_NAME = "command";
    public static final String COMMAND_NUMBER = "command_number";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String COMMAND_TYPE = "command_type";
    public static final String METADATA = "metadata";
    public static final String VISIBLE_TO_SELF = "visible_to_self";
    public static final String VISIBLE_TO_ALL = "visible_to_all";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s INTEGER NOT NULL," +
                    "%3$s INTEGER NOT NULL," +
                    "%4$s INTEGER NOT NULL," +
                    "%5$s VARCHAR(30) NOT NULL," +
                    "%6$s VARCHAR(200) NOT NULL," +
                    "%7$s BOOLEAN NOT NULL," +
                    "%8$s BOOLEAN NOT NULL," +
                    "PRIMARY KEY(%2$s, $3$s)," +
                    "FOREIGN KEY(%3$s)" +
                    "   REFERENCES %9$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%4$s)" +
                    "   REFERENCES %10$s" +
                    "   ON DELETE CASCADE" +
                    ");",
            TABLE_NAME,
            COMMAND_NUMBER,
            GAME_ID,
            PLAYER_ID,
            COMMAND_TYPE,
            METADATA,
            VISIBLE_TO_SELF,
            VISIBLE_TO_ALL,
            DatabaseGame.TABLE_NAME,
            DatabasePlayer.TABLE_NAME
    );

    public DatabaseCommand(String commandNumber, String gameID, String userID, String commandType, String metadata, boolean visibleToSelf, boolean visibleToAll) {
        this.commandNumber = commandNumber;
        this.gameID = gameID;
        this.userID = userID;
        this.commandType = commandType;
        this.metadata = metadata;
        this.visibleToSelf = visibleToSelf;
        this.visibleToAll = visibleToAll;
    }

    public String getCommandNumber() {
        return commandNumber;
    }

    @Override
    public String getGameID() {
        return gameID;
    }

    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public String getCommandType() {
        return commandType;
    }

    @Override
    public String getMetadata() {
        return metadata;
    }

    @Override
    public boolean isVisibleToSelf() {
        return visibleToSelf;
    }

    @Override
    public boolean isVisibleToAll() {
        return visibleToAll;
    }

    public static String columnNames() {
        return  String.join(",", COMMAND_NUMBER, GAME_ID, PLAYER_ID,
                METADATA, VISIBLE_TO_SELF, VISIBLE_TO_ALL);
    }

    private String commandNumber;
    private String gameID;
    private String userID;
    private String commandType;
    private String metadata;
    private boolean visibleToSelf;
    private boolean visibleToAll;
}
