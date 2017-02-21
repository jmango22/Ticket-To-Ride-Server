package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseTrainCard implements IDatabaseTrainCard {
    public static final String ID = "train_card_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String TRAIN_TYPE = "train_type";
    public static final String DISCARDED = "discarded";
    public static final String TABLE_NAME = "train_cards";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE %1$s IF NOT EXISTS (\n" +
                    "    %2$s SERIAL INTEGER NOT NULL,\n" +
                    "    %3$s INTEGER NOT NULL,\n" +
                    "    %4$s INTEGER,\n" +
                    "    %5$s VARCHAR(10),\n" +
                    "    %6$s BOOLEAN,\n" +
                    "    PRIMARY KEY %1$s,\n" +
                    "    FOREIGN KEY(%3$s)\n" +
                    "      REFERENCES %7$s\n" +
                    "      ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY(%2$s)\n" +
                    "      REFERENCES %8$s\n" +
                    "      ON DELETE CASCADE\n" +
                    ");",
            TABLE_NAME,
            ID,
            GAME_ID,
            PLAYER_ID,
            TRAIN_TYPE,
            DISCARDED,
            DatabasePlayer.TABLE_NAME,
            DatabaseGame.TABLE_NAME
    );

    public DatabaseTrainCard(String id,String gameID, String playerID,
                             String trainType, boolean discarded) {
        this.id = id;
        this.gameID = gameID;
        this.playerID = playerID;
        this.trainType = trainType;
        this.discarded = discarded;
    }

    @Override
    public String getID() {
        return id;
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
    public String getTrainType() {
        return trainType;
    }

    @Override
    public boolean isDiscarded() {
        return discarded;
    }

    private String id;
    private String gameID;
    private String playerID;
    private String trainType;
    private boolean discarded;
}
