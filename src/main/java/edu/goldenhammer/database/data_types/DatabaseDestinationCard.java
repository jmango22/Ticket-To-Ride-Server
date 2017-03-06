package edu.goldenhammer.database.data_types;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by seanjib on 2/22/2017.
 */
public class DatabaseDestinationCard implements IDatabaseDestinationCard {
    public static final int MAX_DESTINATION_CARDS = 76;
    public static final String TABLE_NAME = "destination_card";
    public static final String ID = "destination_card_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_ID = "player_id";
    public static final String CITY_1 = "city_1";
    public static final String CITY_2 = "city_2";
    public static final String DISCARDED = "discarded";
    public static final String POINTS = "points";
    public static final String DRAWN = "drawn";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (\n" +
                    "%2$s SERIAL NOT NULL,\n" +
                    "%3$s INTEGER NOT NULL,\n" +
                    "%4$s INTEGER,\n" +
                    "%5$s INTEGER NOT NULL,\n" +
                    "%6$s INTEGER NOT NULL,\n" +
                    "%7$s BOOLEAN NOT NULL DEFAULT false,\n" +
                    "%8$s INTEGER NOT NULL," +
                    "%9$s BOOLEAN NOT NULL DEFAULT false," +
                    "PRIMARY KEY(%2$s),\n" +
                    "FOREIGN KEY(%3$s)" +
                    "   REFERENCES %10$s" +
                    "   ON DELETE CASCADE,\n" +
                    "FOREIGN KEY(%4$s)" +
                    "   REFERENCES %11$s" +
                    "   ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (%5$s)" +
                    "   REFERENCES %12$s" +
                    "   ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (%6$s)" +
                    "   REFERENCES %12$s" +
                    "   ON DELETE CASCADE\n" +
                    ");",
            TABLE_NAME,
            ID,
            GAME_ID,
            PLAYER_ID,
            CITY_1,
            CITY_2,
            DISCARDED,
            POINTS,
            DRAWN,
            DatabaseGame.TABLE_NAME,
            DatabasePlayer.TABLE_NAME,
            DatabaseCity.TABLE_NAME
    );

    public DatabaseDestinationCard(String destinationCardID, String gameID, int city1, int city2,
                                   String playerID, boolean discarded, int points, boolean drawn) {
        this.destinationCardID = destinationCardID;
        this.gameID = gameID;
        this.playerID = playerID;
        this.city1 = city1;
        this.city2 = city2;
        this.discarded = discarded;
        this.points = points;
        this.drawn = drawn;
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
    public int getCity1() {
        return city1;
    }

    @Override
    public int getCity2() {
        return city2;
    }

    @Override
    public boolean isDiscarded() {
        return discarded;
    }
    
    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public boolean isDrawn() {
        return drawn;
    }

    public static String getAllDestinations() {
        String formattedDestination = "";
        for(int i = 0; i < MAX_DESTINATION_CARDS; i++) {
            formattedDestination += getFormattedDestination();
        }
        return formattedDestination.substring(0, formattedDestination.length() - 2);
    }

    private static String getFormattedDestination() {
        return String.format("(%1$s, %2$s, %3$s, ?),\n",
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = ?)",
                        DatabaseGame.ID,
                        DatabaseGame.TABLE_NAME,
                        DatabaseGame.GAME_NAME),
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = ?)",
                        DatabaseCity.ID,
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME),
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = ?)",
                        DatabaseCity.ID,
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME)
        );
    }
    public static DatabaseDestinationCard buildDestinationCardFromResultSet(ResultSet resultSet) throws SQLException {
        return new DatabaseDestinationCard(
                resultSet.getString(ID),
                resultSet.getString(GAME_ID),
                resultSet.getInt(CITY_1),
                resultSet.getInt(CITY_2),
                resultSet.getString(PLAYER_ID),
                resultSet.getBoolean(DISCARDED),
                resultSet.getInt(POINTS),
                resultSet.getBoolean(DRAWN)
        );
    }
    
    private String destinationCardID;
    private String gameID;
    private String playerID;
    private int city1;
    private int city2;
    private boolean discarded;
    private int points;
    private boolean drawn;
}
