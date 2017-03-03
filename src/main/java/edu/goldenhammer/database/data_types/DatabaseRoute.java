package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseRoute implements Serializable, IDatabaseRoute {
    public static final String ROUTE_NUMBER = "route_number";
    public static final String CITY_1 = "city_1";
    public static final String CITY_2 = "city_2";
    public static final String ROUTE_COLOR = "route_color";
    public static final String ROUTE_LENGTH = "route_length";
    public static final String TABLE_NAME = "route";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (\n" +
                    "    %2$s SERIAL NOT NULL,\n" +
                    "    %4$s INTEGER NOT NULL,\n" +
                    "    %5$s INTEGER NOT NULL,\n" +
                    "    %6$s VARCHAR(10) NOT NULL,\n" +
                    "    %7$s INTEGER NOT NULL,\n" +
                    "    PRIMARY KEY(%2$s),\n" +
                    "    FOREIGN KEY(%4$s)" +
                    "       REFERENCES %8$s" +
                    "       ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY(%5$s)" +
                    "       REFERENCES %8$s" +
                    "       ON DELETE CASCADE\n" +
                    ");" +
                    "INSERT INTO %1$s(%3, %4$s, %5$s, %6$s, %7$s) VALUES %9$s",
            TABLE_NAME,
            ROUTE_NUMBER,
            CITY_1,
            CITY_2,
            ROUTE_COLOR,
            ROUTE_LENGTH,
            DatabaseCity.TABLE_NAME,
            getAllRoutes());

    public DatabaseRoute(String id, int city1, int city2, String routeColor, int routeLength) {
        this.id = id;
        this.city1 = city1;
        this.city2 = city2;
        this.routeColor = routeColor;
        this.routeLength = routeLength;
    }

    @Override
    public String getID() {
        return id;
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
    public String getRouteColor() {
        return routeColor;
    }

    @Override
    public int getRouteLength() {
        return routeLength;
    }

    private static String getAllRoutes() {
        String formattedRoute =
                getFormattedRoute(DatabaseCity.AMON_SUL, DatabaseCity.BREE, "gray", 1) +
                getFormattedRoute(DatabaseCity.AMON_SUL, DatabaseCity.BREE, "gray", 1) +
                getFormattedRoute(DatabaseCity.AMON_SUL, DatabaseCity.ETTENMOORS, "red", 3) +
                getFormattedRoute(DatabaseCity.AMON_SUL, DatabaseCity.RIVENDELL, "gray", 2) +
                getFormattedRoute(DatabaseCity.AMON_SUL, DatabaseCity.RIVENDELL, "gray", 2) +
                getFormattedRoute(DatabaseCity.AMON_SUL, DatabaseCity.THARBAD, "orange", 2) +
                getFormattedRoute(DatabaseCity.ASH_MOUNTAINS, DatabaseCity.BARAD_DUR, "red", 2) +
                getFormattedRoute(DatabaseCity.ASH_MOUNTAINS, DatabaseCity.SEA_OF_NURNEN, "gray", 4) +
                getFormattedRoute(DatabaseCity.ASH_MOUNTAINS, DatabaseCity.SEA_OF_RHUN, "blue", 4) +
                getFormattedRoute(DatabaseCity.BARAD_DUR, DatabaseCity.DAGORLAD_BATTLE_PLAINS, "blue", 2) +
                getFormattedRoute(DatabaseCity.BARAD_DUR, DatabaseCity.DAGORLAD_BATTLE_PLAINS, "yellow", 2) +
                getFormattedRoute(DatabaseCity.BARAD_DUR, DatabaseCity.MINAS_MORGUL, "gray", 2) +
                getFormattedRoute(DatabaseCity.BARAD_DUR, DatabaseCity.MINAS_MORGUL, "gray", 2) +
                getFormattedRoute(DatabaseCity.BARAD_DUR, DatabaseCity.SEA_OF_NURNEN, "white", 4) +
                getFormattedRoute(DatabaseCity.BARAD_DUR, DatabaseCity.SEA_OF_RHUN, "orange", 5) +
                getFormattedRoute(DatabaseCity.BREE, DatabaseCity.HOBBITON, "gray", 2) +
                getFormattedRoute(DatabaseCity.BREE, DatabaseCity.HOBBITON, "gray", 2) +
                getFormattedRoute(DatabaseCity.BREE, DatabaseCity.LAKE_EVENDIM, "black", 2) +
                getFormattedRoute(DatabaseCity.BREE, DatabaseCity.LAKE_EVENDIM, "white", 2) +
                getFormattedRoute(DatabaseCity.BREE, DatabaseCity.THARBAD, "gray", 2) +
                getFormattedRoute(DatabaseCity.CROSSINGS_OF_POROS, DatabaseCity.EDHELLOND, "orange", 4) +
                getFormattedRoute(DatabaseCity.CROSSINGS_OF_POROS, DatabaseCity.MINAS_MORGUL, "green", 2) +
                getFormattedRoute(DatabaseCity.CROSSINGS_OF_POROS, DatabaseCity.MINAS_TIRITH, "yellow", 2) +
                getFormattedRoute(DatabaseCity.CROSSINGS_OF_POROS, DatabaseCity.SEA_OF_NURNEN, "violet", 4) +
                getFormattedRoute(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.DOL_GULDUR, "gray", 3) +
                getFormattedRoute(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.EMYN_MUIL, "black", 2) +
                getFormattedRoute(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.EMYN_MUIL, "orange", 2) +
                getFormattedRoute(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.FALLS_OF_RAUROS, "white", 2) +
                getFormattedRoute(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.MINAS_MORGUL, "gray", 2) +
                getFormattedRoute(DatabaseCity.DAGORLAD_BATTLE_PLAINS, DatabaseCity.SEA_OF_RHUN, "green", 6) +
                getFormattedRoute(DatabaseCity.DOL_GULDUR, DatabaseCity.EAST_BIGHT, "yellow", 1) +
                getFormattedRoute(DatabaseCity.DOL_GULDUR, DatabaseCity.EMYN_MUIL, "red", 2) +
                getFormattedRoute(DatabaseCity.DOL_GULDUR, DatabaseCity.LORIEN, "black", 2) +
                getFormattedRoute(DatabaseCity.EAST_BIGHT, DatabaseCity.IRON_HILLS, "red", 6) +
                getFormattedRoute(DatabaseCity.EAST_BIGHT, DatabaseCity.LORIEN, "green", 3) +
                getFormattedRoute(DatabaseCity.EAST_BIGHT, DatabaseCity.RIVENDELL, "orange", 5) +
                getFormattedRoute(DatabaseCity.EAST_BIGHT, DatabaseCity.SEA_OF_RHUN, "violet", 6) +
                getFormattedRoute(DatabaseCity.EAST_BIGHT, DatabaseCity.THE_LONELY_MOUNTAIN, "blue", 4) +
                getFormattedRoute(DatabaseCity.EDHELLOND, DatabaseCity.ERECH, "green", 1) +
                getFormattedRoute(DatabaseCity.EDHELLOND, DatabaseCity.MINAS_TIRITH, "black", 4) +
                getFormattedRoute(DatabaseCity.EDHELLOND, DatabaseCity.RAS_MORTHIL, "black", 5) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.ERECH, "white", 1) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.FALLS_OF_RAUROS, "black", 2) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.HELMS_DEEP, "gray", 1) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.HELMS_DEEP, "gray", 1) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.FANGORN, "yellow", 2) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.MINAS_TIRITH, "violet", 3) +
                getFormattedRoute(DatabaseCity.EDORAS, DatabaseCity.MINAS_TIRITH, "red", 3) +
                getFormattedRoute(DatabaseCity.EMYN_MUIL, DatabaseCity.FALLS_OF_RAUROS, "gray", 1) +
                getFormattedRoute(DatabaseCity.EMYN_MUIL, DatabaseCity.FALLS_OF_RAUROS, "gray", 1) +
                getFormattedRoute(DatabaseCity.EMYN_MUIL, DatabaseCity.FANGORN, "gray", 2) +
                getFormattedRoute(DatabaseCity.EMYN_MUIL, DatabaseCity.FANGORN, "gray", 2) +
                getFormattedRoute(DatabaseCity.ERECH, DatabaseCity.HELMS_DEEP, "orange", 1) +
                getFormattedRoute(DatabaseCity.ERECH, DatabaseCity.RAS_MORTHIL, "blue", 2) +
                getFormattedRoute(DatabaseCity.ERYN_VORN, DatabaseCity.HARLINDON, "blue", 2) +
                getFormattedRoute(DatabaseCity.ERYN_VORN, DatabaseCity.HOBBITON, "black", 3) +
                getFormattedRoute(DatabaseCity.ERYN_VORN, DatabaseCity.THARBAD, "red", 3) +
                getFormattedRoute(DatabaseCity.ERYN_VORN, DatabaseCity.LOND_DAER, "green", 3) +
                getFormattedRoute(DatabaseCity.ETTENMOORS, DatabaseCity.LAKE_EVENDIM, "orange", 6) +
                getFormattedRoute(DatabaseCity.ETTENMOORS, DatabaseCity.RIVENDELL, "blue", 2) +
                getFormattedRoute(DatabaseCity.ETTENMOORS, DatabaseCity.RIVENDELL, "green", 2) +
                getFormattedRoute(DatabaseCity.ETTENMOORS, DatabaseCity.THE_LONELY_MOUNTAIN, "gray", 6) +
                getFormattedRoute(DatabaseCity.FALLS_OF_RAUROS, DatabaseCity.MINAS_TIRITH, "gray", 1) +
                getFormattedRoute(DatabaseCity.FALLS_OF_RAUROS, DatabaseCity.MINAS_TIRITH, "gray", 1) +
                getFormattedRoute(DatabaseCity.FANGORN, DatabaseCity.HELMS_DEEP, "white", 2) +
                getFormattedRoute(DatabaseCity.FANGORN, DatabaseCity.ISENGARD, "black", 2) +
                getFormattedRoute(DatabaseCity.FANGORN, DatabaseCity.ISENGARD, "green", 2) +
                getFormattedRoute(DatabaseCity.FANGORN, DatabaseCity.LORIEN, "gray", 2) +
                getFormattedRoute(DatabaseCity.FANGORN, DatabaseCity.LORIEN, "gray", 2) +
                getFormattedRoute(DatabaseCity.FORLINDON, DatabaseCity.GREY_HAVENS, "violet", 2) +
                getFormattedRoute(DatabaseCity.FORLINDON, DatabaseCity.HARLINDON, "red", 4) +
                getFormattedRoute(DatabaseCity.FORLINDON, DatabaseCity.LAKE_EVENDIM, "blue", 5) +
                getFormattedRoute(DatabaseCity.GREY_HAVENS, DatabaseCity.HARLINDON, "white", 3) +
                getFormattedRoute(DatabaseCity.GREY_HAVENS, DatabaseCity.HOBBITON, "green", 2) +
                getFormattedRoute(DatabaseCity.GREY_HAVENS, DatabaseCity.HOBBITON, "orange", 2) +
                getFormattedRoute(DatabaseCity.GREY_HAVENS, DatabaseCity.LAKE_EVENDIM, "gray", 3) +
                getFormattedRoute(DatabaseCity.HARLINDON, DatabaseCity.HOBBITON, "yellow", 4) +
                getFormattedRoute(DatabaseCity.HELMS_DEEP, DatabaseCity.ISENGARD, "gray", 2) +
                getFormattedRoute(DatabaseCity.HELMS_DEEP, DatabaseCity.ISENGARD, "gray", 2) +
                getFormattedRoute(DatabaseCity.HELMS_DEEP, DatabaseCity.RAS_MORTHIL, "violet", 6) +
                getFormattedRoute(DatabaseCity.HOBBITON, DatabaseCity.LAKE_EVENDIM, "red", 1) +
                getFormattedRoute(DatabaseCity.HOBBITON, DatabaseCity.LAKE_EVENDIM, "violet", 1) +
                getFormattedRoute(DatabaseCity.IRON_HILLS, DatabaseCity.SEA_OF_RHUN, "yellow", 5) +
                getFormattedRoute(DatabaseCity.IRON_HILLS, DatabaseCity.THE_LONELY_MOUNTAIN, "black", 3) +
                getFormattedRoute(DatabaseCity.ISENGARD, DatabaseCity.LOND_DAER, "orange", 3) +
                getFormattedRoute(DatabaseCity.ISENGARD, DatabaseCity.MORIAS_GATE, "blue", 2) +
                getFormattedRoute(DatabaseCity.ISENGARD, DatabaseCity.MORIAS_GATE, "red", 2) +
                getFormattedRoute(DatabaseCity.ISENGARD, DatabaseCity.RAS_MORTHIL, "violet", 6) +
                getFormattedRoute(DatabaseCity.ISENGARD, DatabaseCity.THARBAD, "gray", 3) +
                getFormattedRoute(DatabaseCity.LOND_DAER, DatabaseCity.RAS_MORTHIL, "white", 5) +
                getFormattedRoute(DatabaseCity.LOND_DAER, DatabaseCity.THARBAD, "yellow", 3) +
                getFormattedRoute(DatabaseCity.LORIEN, DatabaseCity.MORIAS_GATE, "white", 2) +
                getFormattedRoute(DatabaseCity.LORIEN, DatabaseCity.RIVENDELL, "gray", 2) +
                getFormattedRoute(DatabaseCity.LORIEN, DatabaseCity.RIVENDELL, "gray", 2) +
                getFormattedRoute(DatabaseCity.MINAS_MORGUL, DatabaseCity.MINAS_TIRITH, "gray", 2) +
                getFormattedRoute(DatabaseCity.MINAS_MORGUL, DatabaseCity.MINAS_TIRITH, "gray", 2) +
                getFormattedRoute(DatabaseCity.MINAS_MORGUL, DatabaseCity.SEA_OF_NURNEN, "blue", 5) +
                getFormattedRoute(DatabaseCity.MORIAS_GATE, DatabaseCity.RIVENDELL, "violet", 2) +
                getFormattedRoute(DatabaseCity.MORIAS_GATE, DatabaseCity.RIVENDELL, "yellow", 2) +
                getFormattedRoute(DatabaseCity.MORIAS_GATE, DatabaseCity.THARBAD, "green", 2) +
                getFormattedRoute(DatabaseCity.RIVENDELL, DatabaseCity.THE_LONELY_MOUNTAIN, "white", 6);
        return formattedRoute.substring(0, formattedRoute.length() - 2) + ";"; //replaces the final comma with a semicolon
    }

    private static String getFormattedRoute(String startCity, String endCity, String color, int length) {
        return String.format("(%1$s, %2$s, '%3$s', %4$d),\n",
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = '%4$s')",
                        DatabaseCity.ID,
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME,
                        startCity),
                String.format("(SELECT %1$s FROM %2$s WHERE %3$s = '%4$s')\n",
                        DatabaseCity.ID,
                        DatabaseCity.TABLE_NAME,
                        DatabaseCity.NAME,
                        endCity),
                color,
                length
        );
    }

    public static String columnNames() {
        return String.join(",",ROUTE_NUMBER,CITY_1,CITY_2,ROUTE_COLOR,ROUTE_LENGTH);
    }

    String id;
    int city1;
    int city2;
    String routeColor;
    int routeLength;
}
