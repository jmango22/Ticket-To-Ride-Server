package edu.goldenhammer.database.data_types;

import javax.xml.crypto.Data;

/**
 * Created by seanjib on 2/22/2017.
 */
public class DatabaseDestinationCard implements IDatabaseDestinationCard {
    public static final String TABLE_NAME = "destination_card";
    public static final String ID = "destination_card_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String CITY_1 = "city_1";
    public static final String CITY_2 = "city_2";
    public static final String DISCARDED = "discarded";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL INTEGER NOT NULL," +
                    "%3$s INTEGER NOT NULL," +
                    "%4$s INTEGER," +
                    "%5$s INTEGER NOT NULL," +
                    "%6$s INTEGER NOT NULL," +
                    "%7$s BOOLEAN," +
                    "PRIMARY KEY %8$s," +
                    "FOREIGN KEY(%9$s)" +
                    "   REFERENCES %10$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%11$s)" +
                    "   REFERENCES %12$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%13$s)" +
                    "   REFERENCES %14$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%15$s)" +
                    "   REFERENCES %16$s" +
                    "   ON DELETE CASCADE" +
                    ");",
            TABLE_NAME,
            ID,
            GAME_ID,
            PLAYER_ID,
            CITY_1,
            CITY_2,
            DISCARDED,
            ID,
            GAME_ID,
            DatabaseGame.TABLE_NAME,
            PLAYER_ID,
            DatabasePlayer.TABLE_NAME,
            CITY_1,
            DatabaseCity.TABLE_NAME,
            CITY_2,
            DatabaseCity.TABLE_NAME
    );

    public DatabaseDestinationCard(String destinationCardID, String gameID, String city1, String city2, String playerID, boolean discarded) {
        this.destinationCardID = destinationCardID;
        this.gameID = gameID;
        this.playerID = playerID;
        this.city1 = city1;
        this.city2 = city2;
        this.discarded = discarded;
    }

    @Override
    public String getID() {
        return destinationCardID;
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
    public String getCity1() {
        return city1;
    }

    @Override
    public String getCity2() {
        return city2;
    }

    @Override
    public boolean isDiscarded() {
        return discarded;
    }

    private String destinationCardID;
    private String gameID;
    private String playerID;
    private String city1;
    private String city2;
    private boolean discarded;
}
