package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/22/2017.
 */
public class DatabaseMessage implements IDatabaseMessage {
    public static final String TABLE_NAME = "chat_message";
    public static final String ID = "message_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String MESSAGE = "message";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL INTEGER NOT NULL," +
                    "%3$s INTEGER NOT NULL," +
                    "%4$s INTEGER NOT NULL," +
                    "%5$s VARCHAR(120)," +
                    "PRIMARY KEY %6$s," +
                    "FOREIGN KEY(%7$s)" +
                    "   REFERENCES %8$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%9$s)" +
                    "   REFERENCES %10$s" +
                    "   ON DELETE CASCADE",
            TABLE_NAME,
            ID,
            GAME_ID,
            PLAYER_ID,
            MESSAGE,
            ID,
            GAME_ID,
            DatabaseGame.TABLE_NAME,
            PLAYER_ID,
            DatabasePlayer.TABLE_NAME
    );


    public DatabaseMessage(String messageID, String gameID, String playerID, String message) {
        this.messageID = messageID;
        this.gameID = gameID;
        this.playerID = playerID;
        this.message = message;
    }

    @Override
    public String getID() {
        return messageID;
    }

    @Override
    public String getGameID() {
        return gameID;
    }

    @Override
    public String getPlayerID() {
        return playerID;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static String columnNames() {
        return String.join(",", ID, GAME_ID, PLAYER_ID, MESSAGE);
    }

    private String messageID;
    private String gameID;
    private String playerID;
    private String message;
}
