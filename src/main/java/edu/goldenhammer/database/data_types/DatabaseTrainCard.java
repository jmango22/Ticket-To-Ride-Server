package edu.goldenhammer.database.data_types;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseTrainCard implements IDatabaseTrainCard {
    public static final int MAX_COLORED_CARDS = 12;
    public static final int MAX_WILD_CARDS = 14;
    public static final int MAX_STARTING_CARDS = 4;
    public static final String ID = "train_card_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String TRAIN_TYPE = "train_type";
    public static final String DISCARDED = "discarded";
    public static final String TABLE_NAME = "train_card";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (\n" +
                    "    %2$s INTEGER NOT NULL,\n" +
                    "    %3$s INTEGER NOT NULL,\n" +
                    "    %4$s INTEGER,\n" +
                    "    %5$s VARCHAR(10),\n" +
                    "    %6$s BOOLEAN NOT NULL DEFAULT false,\n" +
                    "    PRIMARY KEY(%2$s, %3$s),\n" +
                    "    FOREIGN KEY(%4$s)\n" +
                    "      REFERENCES %7$s\n" +
                    "      ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY(%3$s)\n" +
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

    public static String getAllTrainCards() {
        String sqlString = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < MAX_COLORED_CARDS; j++) {
                sqlString += String.format("(?, (SELECT %1$s FROM %2$s WHERE %3$s = ?), ",
                        DatabaseGame.ID,
                        DatabaseGame.TABLE_NAME,
                        DatabaseGame.GAME_NAME
                );

                switch (i) {
                    case 0:
                        sqlString += "'red'),";
                        break;
                    case 1:
                        sqlString += "'orange'),";
                        break;
                    case 2:
                        sqlString += "'yellow'),";
                        break;
                    case 3:
                        sqlString += "'green'),";
                        break;
                    case 4:
                        sqlString += "'blue'),";
                        break;
                    case 5:
                        sqlString += "'violet'),";
                        break;
                    case 6:
                        sqlString += "'black'),";
                        break;
                    case 7:
                        sqlString += "'white'),";
                        break;
                }
            }
        }

        for(int i = 0; i < MAX_WILD_CARDS; i++) {
            sqlString += String.format("(?, (SELECT %1$s FROM %2$s WHERE %3$s = ?), 'wild')",
                    DatabaseGame.ID,
                    DatabaseGame.TABLE_NAME,
                    DatabaseGame.GAME_NAME
            );
            if(i != MAX_WILD_CARDS - 1) {
                sqlString += ",";
            }
            else {
                sqlString += ";";
            }
        }
        return sqlString;
    }

    public static DatabaseTrainCard buildTrainCardFromResultSet(ResultSet resultSet) throws SQLException{
        return new DatabaseTrainCard(
                resultSet.getString(ID),
                resultSet.getString(GAME_ID),
                resultSet.getString(PLAYER_ID),
                resultSet.getString(TRAIN_TYPE),
                resultSet.getBoolean(DISCARDED)
        );
    }

    private String id;
    private String gameID;
    private String playerID;
    private String trainType;
    private boolean discarded;
}
