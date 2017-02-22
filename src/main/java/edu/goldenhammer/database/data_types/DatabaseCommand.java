package edu.goldenhammer.database.data_types;

import org.omg.PortableInterceptor.USER_EXCEPTION;

/**
 * Created by seanjib on 2/22/2017.
 */
public class DatabaseCommand implements IDatabaseCommand{

    public static final String TABLE_NAME = "command";
    public static final String COMMAND_ID = "command_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String METADATA = "metadata";
    public static final String VISIBLE_TO_SELF = "visible_to_self";
    public static final String VISIBLE_TO_ALL = "visible_to_all";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL INTEGER NOT NULL," +
                    "%3$s INTEGER NOT NULL," +
                    "%4$s INTEGER NOT NULL," +
                    "%5$s VARCHAR(200) NOT NULL," +
                    "%6$s BOOLEAN NOT NULL," +
                    "%7$s BOOLEAN NOT NULL," +
                    "PRIMARY KEY %5$s," +
                    "FOREIGN KEY(%5$s)" +
                    "   REFERENCES %5$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%5$s)" +
                    "   REFERENCES %5$s" +
                    "   ON DELETE CASCADE" +
                    ");",
            TABLE_NAME,
            COMMAND_ID,
            GAME_ID,
            PLAYER_ID,
            METADATA,
            VISIBLE_TO_SELF,
            VISIBLE_TO_ALL,
            COMMAND_ID,
            GAME_ID,
            DatabaseGame.TABLE_NAME,
            PLAYER_ID,
            DatabasePlayer.TABLE_NAME
    );

    public DatabaseCommand(String commandID, String gameID, String userID, String metadata, boolean visibleToSelf, boolean visibleToAll) {
        this.commandID = commandID;
        this.gameID = gameID;
        this.userID = userID;
        this.metadata = metadata;
        this.visibleToSelf = visibleToSelf;
        this.visibleToAll = visibleToAll;
    }

    @Override
    public String getCommandID() {
        return commandID;
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
        return  String.join(",", COMMAND_ID, GAME_ID, PLAYER_ID,
                METADATA, VISIBLE_TO_SELF, VISIBLE_TO_ALL);
    }

    private String commandID;
    private String gameID;
    private String userID;
    private String metadata;
    private boolean visibleToSelf;
    private boolean visibleToAll;
}
