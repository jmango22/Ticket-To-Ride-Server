package edu.goldenhammer.database.data_types;

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
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (\n" +
                    "%2$s SERIAL NOT NULL,\n" +
                    "%3$s INTEGER NOT NULL,\n" +
                    "%4$s INTEGER,\n" +
                    "%5$s INTEGER NOT NULL,\n" +
                    "%6$s INTEGER NOT NULL,\n" +
                    "%7$s BOOLEAN NOT NULL DEFAULT false,\n" +
                    "%8$s INTEGER NOT NULL," +
                    "PRIMARY KEY(%2$s),\n" +
                    "FOREIGN KEY(%3$s)" +
                    "   REFERENCES %9$s" +
                    "   ON DELETE CASCADE,\n" +
                    "FOREIGN KEY(%4$s)" +
                    "   REFERENCES %10$s" +
                    "   ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (%5$s)" +
                    "   REFERENCES %11$s" +
                    "   ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (%6$s)" +
                    "   REFERENCES %11$s" +
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
            DatabaseGame.TABLE_NAME,
            DatabasePlayer.TABLE_NAME,
            DatabaseCity.TABLE_NAME
    );

    public DatabaseDestinationCard(String destinationCardID, String gameID, int city1, int city2,
                                   String playerID, boolean discarded, int points) {
        this.destinationCardID = destinationCardID;
        this.gameID = gameID;
        this.playerID = playerID;
        this.city1 = city1;
        this.city2 = city2;
        this.discarded = discarded;
        this.points = points;
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

    public static String getAllDestinations() {
        String formattedDestination =
                getFormattedDestination(DatabaseCity.AMON_SUL, DatabaseCity.CROSSINGS_OF_POROS, 12) +
                getFormattedDestination(DatabaseCity.AMON_SUL, DatabaseCity.DOL_GULDUR, 6) +
                getFormattedDestination(DatabaseCity.AMON_SUL, DatabaseCity.FALLS_OF_RAUROS, 9) +
                getFormattedDestination(DatabaseCity.AMON_SUL, DatabaseCity.LORIEN, 4) +
                getFormattedDestination(DatabaseCity.BREE, DatabaseCity.DAGORLAD_BATTLE_PLAINS, 10) +
                getFormattedDestination(DatabaseCity.BREE, DatabaseCity.EDHELLOND, 9) +
                getFormattedDestination(DatabaseCity.BREE, DatabaseCity.ERECH, 8) +
                getFormattedDestination(DatabaseCity.BREE, DatabaseCity.MINAS_MORGUL, 12) +
                getFormattedDestination(DatabaseCity.CROSSINGS_OF_POROS, DatabaseCity.ASH_MOUNTAINS, 8) +
                getFormattedDestination(DatabaseCity.CROSSINGS_OF_POROS, DatabaseCity.THE_LONELY_MOUNTAIN, 11) +
                getFormattedDestination(DatabaseCity.EDHELLOND, DatabaseCity.FALLS_OF_RAUROS, 5) +
                getFormattedDestination(DatabaseCity.EDORAS, DatabaseCity.EAST_BIGHT, 6) +
                getFormattedDestination(DatabaseCity.EDORAS, DatabaseCity.MINAS_MORGUL, 4) +
                getFormattedDestination(DatabaseCity.EDORAS, DatabaseCity.SEA_OF_RHUN, 10) +
                getFormattedDestination(DatabaseCity.ERECH, DatabaseCity.DAGORLAD_BATTLE_PLAINS, 5) +
                getFormattedDestination(DatabaseCity.ERECH, DatabaseCity.IRON_HILLS, 14) +
                getFormattedDestination(DatabaseCity.ERECH, DatabaseCity.MINAS_MORGUL, 6) +
                getFormattedDestination(DatabaseCity.ERYN_VORN, DatabaseCity.ASH_MOUNTAINS, 16) +
                getFormattedDestination(DatabaseCity.ERYN_VORN, DatabaseCity.DOL_GULDUR, 9) +
                getFormattedDestination(DatabaseCity.ERYN_VORN, DatabaseCity.EAST_BIGHT, 10) +
                getFormattedDestination(DatabaseCity.ERYN_VORN, DatabaseCity.THE_LONELY_MOUNTAIN, 13) +
                getFormattedDestination(DatabaseCity.ETTENMOORS, DatabaseCity.EDHELLOND, 10) +
                getFormattedDestination(DatabaseCity.ETTENMOORS, DatabaseCity.HELMS_DEEP, 8) +
                getFormattedDestination(DatabaseCity.ETTENMOORS, DatabaseCity.SEA_OF_NURNEN, 15) +
                getFormattedDestination(DatabaseCity.ETTENMOORS, DatabaseCity.SEA_OF_RHUN, 13) +
                getFormattedDestination(DatabaseCity.FANGORN, DatabaseCity.BARAD_DUR, 6) +
                getFormattedDestination(DatabaseCity.FANGORN, DatabaseCity.SEA_OF_RHUN, 10) +
                getFormattedDestination(DatabaseCity.FORLINDON, DatabaseCity.BARAD_DUR, 18) +
                getFormattedDestination(DatabaseCity.FORLINDON, DatabaseCity.EAST_BIGHT, 14) +
                getFormattedDestination(DatabaseCity.FORLINDON, DatabaseCity.RAS_MORTHIL, 14) +
                getFormattedDestination(DatabaseCity.FORLINDON, DatabaseCity.SEA_OF_NURNEN, 23) +
                getFormattedDestination(DatabaseCity.GREY_HAVENS, DatabaseCity.ASH_MOUNTAINS, 18) +
                getFormattedDestination(DatabaseCity.GREY_HAVENS, DatabaseCity.DAGORLAD_BATTLE_PLAINS, 14) +
                getFormattedDestination(DatabaseCity.GREY_HAVENS, DatabaseCity.EMYN_MUIL, 13) +
                getFormattedDestination(DatabaseCity.GREY_HAVENS, DatabaseCity.RIVENDELL, 7) +
                getFormattedDestination(DatabaseCity.HARLINDON, DatabaseCity.IRON_HILLS, 18) +
                getFormattedDestination(DatabaseCity.HARLINDON, DatabaseCity.ISENGARD, 8) +
                getFormattedDestination(DatabaseCity.HARLINDON, DatabaseCity.LORIEN, 9) +
                getFormattedDestination(DatabaseCity.HARLINDON, DatabaseCity.SEA_OF_NURNEN, 20) +
                getFormattedDestination(DatabaseCity.HELMS_DEEP, DatabaseCity.EMYN_MUIL, 4) +
                getFormattedDestination(DatabaseCity.HELMS_DEEP, DatabaseCity.THE_LONELY_MOUNTAIN, 11) +
                getFormattedDestination(DatabaseCity.HOBBITON, DatabaseCity.ASH_MOUNTAINS, 16) +
                getFormattedDestination(DatabaseCity.HOBBITON, DatabaseCity.BARAD_DUR, 16) +
                getFormattedDestination(DatabaseCity.HOBBITON, DatabaseCity.DOL_GULDUR, 9) +
                getFormattedDestination(DatabaseCity.HOBBITON, DatabaseCity.ISENGARD, 7) +
                getFormattedDestination(DatabaseCity.HOBBITON, DatabaseCity.THE_LONELY_MOUNTAIN, 11) +
                getFormattedDestination(DatabaseCity.IRON_HILLS, DatabaseCity.SEA_OF_NURNEN, 13) +
                getFormattedDestination(DatabaseCity.ISENGARD, DatabaseCity.BARAD_DUR, 9) +
                getFormattedDestination(DatabaseCity.ISENGARD, DatabaseCity.MINAS_TIRITH, 6) +
                getFormattedDestination(DatabaseCity.ISENGARD, DatabaseCity.THE_LONELY_MOUNTAIN, 11) +
                getFormattedDestination(DatabaseCity.LAKE_EVENDIM, DatabaseCity.EDORAS, 11) +
                getFormattedDestination(DatabaseCity.LAKE_EVENDIM, DatabaseCity.FALLS_OF_RAUROS, 12) +
                getFormattedDestination(DatabaseCity.LAKE_EVENDIM, DatabaseCity.FANGORN, 9) +
                getFormattedDestination(DatabaseCity.LAKE_EVENDIM, DatabaseCity.HELMS_DEEP, 9) +
                getFormattedDestination(DatabaseCity.LAKE_EVENDIM, DatabaseCity.IRON_HILLS, 14) +
                getFormattedDestination(DatabaseCity.LOND_DAER, DatabaseCity.CROSSINGS_OF_POROS, 11) +
                getFormattedDestination(DatabaseCity.LOND_DAER, DatabaseCity.EAST_BIGHT, 10) +
                getFormattedDestination(DatabaseCity.LOND_DAER, DatabaseCity.LORIEN, 7) +
                getFormattedDestination(DatabaseCity.LOND_DAER, DatabaseCity.RIVENDELL, 7) +
                getFormattedDestination(DatabaseCity.LORIEN, DatabaseCity.MINAS_MORGUL, 7) +
                getFormattedDestination(DatabaseCity.MINAS_TIRITH, DatabaseCity.BARAD_DUR, 3) +
                getFormattedDestination(DatabaseCity.MINAS_TIRITH, DatabaseCity.IRON_HILLS, 14) +
                getFormattedDestination(DatabaseCity.MORIAS_GATE, DatabaseCity.EAST_BIGHT, 5) +
                getFormattedDestination(DatabaseCity.MORIAS_GATE, DatabaseCity.FANGORN, 4) +
                getFormattedDestination(DatabaseCity.MORIAS_GATE, DatabaseCity.MINAS_TIRITH, 8) +
                getFormattedDestination(DatabaseCity.RAS_MORTHIL, DatabaseCity.ASH_MOUNTAINS, 13) +
                getFormattedDestination(DatabaseCity.RAS_MORTHIL, DatabaseCity.DOL_GULDUR, 10) +
                getFormattedDestination(DatabaseCity.RAS_MORTHIL, DatabaseCity.MORIAS_GATE, 8) +
                getFormattedDestination(DatabaseCity.RAS_MORTHIL, DatabaseCity.SEA_OF_RHUN, 16) +
                getFormattedDestination(DatabaseCity.RIVENDELL, DatabaseCity.EMYN_MUIL, 6) +
                getFormattedDestination(DatabaseCity.RIVENDELL, DatabaseCity.FALLS_OF_RAUROS, 7) +
                getFormattedDestination(DatabaseCity.THARBAD, DatabaseCity.EDHELLOND, 7) +
                getFormattedDestination(DatabaseCity.THARBAD, DatabaseCity.EMYN_MUIL, 7) +
                getFormattedDestination(DatabaseCity.THARBAD, DatabaseCity.FALLS_OF_RAUROS, 8) +
                getFormattedDestination(DatabaseCity.THARBAD, DatabaseCity.SEA_OF_RHUN, 15) +
                getFormattedDestination(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.THE_LONELY_MOUNTAIN, 8);
        return formattedDestination.substring(0, formattedDestination.length() - 2);
    }

    private static String getFormattedDestination(String startCity, String endCity, int points) {
        return String.format("(%1$s, %2$s, %3$s, %4$s),\n",
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = ?)",
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
    private int city1;
    private int city2;
    private boolean discarded;
    private int points;
}
