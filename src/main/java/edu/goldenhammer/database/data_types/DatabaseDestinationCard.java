package edu.goldenhammer.database.data_types;

import javax.xml.crypto.Data;

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
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL NOT NULL," +
                    "%3$s INTEGER NOT NULL," +
                    "%4$s INTEGER," +
                    "%5$s VARCHAR(20) NOT NULL," +
                    "%6$s VARCHAR(20) NOT NULL," +
                    "%7$s BOOLEAN NOT NULL DEFAULT false," +
                    "PRIMARY KEY(%2$s)," +
                    "FOREIGN KEY(%3$s)" +
                    "   REFERENCES %8$s" +
                    "   ON DELETE CASCADE," +
                    "FOREIGN KEY(%4$s)" +
                    "   REFERENCES %9$s" +
                    "   ON DELETE CASCADE" +
                    ");" +
            "INSERT INTO %1$s(%3$s, %5$s, %6$s, %7$s) VALUES " +
                    "%10$s;",
            TABLE_NAME,
            ID,
            GAME_ID,
            PLAYER_ID,
            CITY_1,
            CITY_2,
            DISCARDED,
            DatabaseGame.TABLE_NAME,
            DatabasePlayer.TABLE_NAME,
            getAllDestinations()
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
    
    @Override
    public int getPoints() {
        return points;
    }

    public static String getAllDestinations() {
        String formattedDestination =
                getFormattedDestination("Amon Sul", "Crossings of Poros", 12) +
                getFormattedDestination("Amon Sul", "Dol Guldur", 6) +
                getFormattedDestination("Amon Sul", "Falls of Rauros", 9) +
                getFormattedDestination("Amon Sul", "Lorien", 4) +
                getFormattedDestination("Bree", "Dagorlad (Battle Plains)", 10) +
                getFormattedDestination("Bree", "Edhellond", 9) +
                getFormattedDestination("Bree", "Erech", 8) +
                getFormattedDestination("Bree", "Minas Morgul", 12) +
                getFormattedDestination("Crossings of Poros", "Ash Mountains", 8) +
                getFormattedDestination("Crossings of Poros", "The Lonely Mountain", 11) +
                getFormattedDestination("Edhellond", "Falls of Rauros", 5) +
                getFormattedDestination("Edoras", "East Bight", 6) +
                getFormattedDestination("Edoras", "Minas Morgul", 4) +
                getFormattedDestination("Edoras", "Sea of Rhun", 10) +
                getFormattedDestination("Erech", "Dagorlad (Battle Plains)", 5) +
                getFormattedDestination("Erech", "Iron Hills", 14) +
                getFormattedDestination("Erech", "Minas Morgul", 6) +
                getFormattedDestination("Eryn Vorn", "Ash Mountains", 16) +
                getFormattedDestination("Eryn Vorn", "Dol Guldur", 9) +
                getFormattedDestination("Eryn Vorn", "East Bight", 10) +
                getFormattedDestination("Eryn Vorn", "The Lonely Mountain", 13) +
                getFormattedDestination("Ettenmoors", "Edhellond", 10) +
                getFormattedDestination("Ettenmoors", "Helm's Deep", 8) +
                getFormattedDestination("Ettenmoors", "Sea of Nurnen", 15) +
                getFormattedDestination("Ettenmoors", "Sea of Rhun", 13) +
                getFormattedDestination("Fangorn", "Barad-Dur", 6) +
                getFormattedDestination("Fangorn", "Sea of Rhun", 10) +
                getFormattedDestination("Forlindon", "Barad-Dur", 18) +
                getFormattedDestination("Forlindon", "East Bight", 14) +
                getFormattedDestination("Forlindon", "Ras Morthil", 14) +
                getFormattedDestination("Forlindon", "Sea of Nurnen", 23) +
                getFormattedDestination("Grey Havens", "Ash Mountains", 18) +
                getFormattedDestination("Grey Havens", "Dagorlad (Battle Plains)", 14) +
                getFormattedDestination("Grey Havens", "Emyn Muil", 13) +
                getFormattedDestination("Grey Havens", "Rivendell", 7) +
                getFormattedDestination("Harlindon", "Iron Hills", 18) +
                getFormattedDestination("Harlindon", "Isengard", 8) +
                getFormattedDestination("Harlindon", "Lorien", 9) +
                getFormattedDestination("Harlindon", "Sea of Nurnen", 20) +
                getFormattedDestination("Helm's Deep", "Emyn Muil", 4) +
                getFormattedDestination("Helm's Deep", "The Lonely Mountain", 11) +
                getFormattedDestination("Hobbiton", "Ash Mountains", 16) +
                getFormattedDestination("Hobbiton", "Barad-Dur", 16) +
                getFormattedDestination("Hobbiton", "Dol Guldur", 9) +
                getFormattedDestination("Hobbiton", "Isengard", 7) +
                getFormattedDestination("Hobbiton", "The Lonely Mountain", 11) +
                getFormattedDestination("Iron Hills", "Sea of Nurnen", 13) +
                getFormattedDestination("Isengard", "Barad-Dur", 9) +
                getFormattedDestination("Isengard", "Minas Tirith", 6) +
                getFormattedDestination("Isengard", "The Lonely Mountain", 11) +
                getFormattedDestination("Lake Evendum", "Edoras", 11) +
                getFormattedDestination("Lake Evendum", "Falls of Rauros", 12) +
                getFormattedDestination("Lake Evendum", "Fangorn", 9) +
                getFormattedDestination("Lake Evendum", "Helm's Deep", 9) +
                getFormattedDestination("Lake Evendum", "Iron Hills", 14) +
                getFormattedDestination("Lond Daer", "Crossings of Poros", 11) +
                getFormattedDestination("Lond Daer", "East Bight", 10) +
                getFormattedDestination("Lond Daer", "Lorien", 7) +
                getFormattedDestination("Lond Daer", "Rivendell", 7) +
                getFormattedDestination("Lorien", "Minas Morgul", 7) +
                getFormattedDestination("Minas Tirith", "Barad-Dur", 3) +
                getFormattedDestination("Minas Tirith", "Iron Hills", 14) +
                getFormattedDestination("Moria's Gate", "East Bight", 5) +
                getFormattedDestination("Moria's Gate", "Fangorn", 4) +
                getFormattedDestination("Moria's Gate", "Minas Tirith", 8) +
                getFormattedDestination("Ras Morthil", "Ash Mountains", 13) +
                getFormattedDestination("Ras Morthil", "Dol Guldur", 10) +
                getFormattedDestination("Ras Morthil", "Moria's Gate", 8) +
                getFormattedDestination("Ras Morthil", "Sea of Rhun", 16) +
                getFormattedDestination("Rivendell", "Emyn Muil", 6) +
                getFormattedDestination("Rivindell", "Falls of Rauros", 7) +
                getFormattedDestination("Tharbad", "Edhellond", 7) +
                getFormattedDestination("Tharbad", "Emyn Muil", 7) +
                getFormattedDestination("Tharbad", "Falls of Rauros", 8) +
                getFormattedDestination("Tharbad", "Sea of Rhun", 15) +
                getFormattedDestination("Dagorlad (Battle Plains)", "The Lonely Mountain", 8);
        return formattedDestination.substring(0, formattedDestination.length() - 1);
    }

    private static String getFormattedDestination(String startCity, String endCity, int points) {
        return String.format("(%1$s, %2$s, %3$s, %4$s),",
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = '?'",
                        DatabaseGame.ID,
                        DatabaseGame.TABLE_NAME,
                        DatabaseGame.GAME_NAME),
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = '%4$s')",
                        DatabaseCity.ID,
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME,
                        startCity),
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = '%4$s')",
                        DatabaseCity.ID,
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME,
                        endCity),
                points
        );
    }

    private String destinationCardID;
    private String gameID;
    private String playerID;
    private String city1;
    private String city2;
    private boolean discarded;
    private int points;
}
