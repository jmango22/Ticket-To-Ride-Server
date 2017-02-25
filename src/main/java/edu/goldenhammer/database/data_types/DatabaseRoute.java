package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseRoute implements Serializable, IDatabaseRoute {
    public static final String ID = "route_id";
    public static final String CITY_1 = "city_1";
    public static final String CITY_2 = "city_2";
    public static final String ROUTE_COLOR = "route_color";
    public static final String ROUTE_LENGTH = "route_length";
    public static final String TABLE_NAME = "route";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (\n" +
                    "    %2$s SERIAL NOT NULL,\n" +
                    "    %3$s INTEGER NOT NULL,\n" +
                    "    %4$s INTEGER NOT NULL,\n" +
                    "    %5$s VARCHAR(10) NOT NULL,\n" +
                    "    %6$s INTEGER NOT NULL,\n" +
                    "    PRIMARY KEY(%2$s),\n" +
                    "    FOREIGN KEY(%3$s)" +
                    "       REFERENCES %7$s" +
                    "       ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY(%4$s)" +
                    "       REFERENCES %7$s" +
                    "       ON DELETE CASCADE\n" +
                    ");" +
                    "INSERT INTO %1$s(%3$s, %4$s, %5$s, %6$s) VALUES %8$s",
            TABLE_NAME,
            ID,
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
                getFormattedRoute("Amon Sul", "Bree", "gray", 1) +
                getFormattedRoute("Amon Sul", "Bree", "gray", 1) +
                getFormattedRoute("Amon Sul", "Ettenmoors", "red", 3) +
                getFormattedRoute("Amon Sul", "Rivendell", "gray", 2) +
                getFormattedRoute("Amon Sul", "Rivendell", "gray", 2) +
                getFormattedRoute("Amon Sul", "Tharbad", "orange", 2) +
                getFormattedRoute("Ash Mountains", "Barad Dur", "red", 2) +
                getFormattedRoute("Ash Mountains", "Sea of Nurnen", "gray", 4) +
                getFormattedRoute("Ash Mountains", "Sea of Rhun", "blue", 4) +
                getFormattedRoute("Barad Dur", "Dagorlad (Battle Plains)", "blue", 2) +
                getFormattedRoute("Barad Dur", "Dagorlad (Battle Plains)", "yellow", 2) +
                getFormattedRoute("Barad Dur", "Minas Morgul", "gray", 2) +
                getFormattedRoute("Barad Dur", "Minas Morgul", "gray", 2) +
                getFormattedRoute("Barad Dur", "Sea of Nurnen", "white", 4) +
                getFormattedRoute("Barad Dur", "Sea of Rhun", "orange", 5) +
                getFormattedRoute("Bree", "Hobbiton", "gray", 2) +
                getFormattedRoute("Bree", "Hobbiton", "gray", 2) +
                getFormattedRoute("Bree", "Lake Evendim", "black", 2) +
                getFormattedRoute("Bree", "Lake Evendim", "white", 2) +
                getFormattedRoute("Bree", "Tharbad", "gray", 2) +
                getFormattedRoute("Crossings of Poros", "Edhellond", "orange", 4) +
                getFormattedRoute("Crossings of Poros", "Minas Morgul", "green", 2) +
                getFormattedRoute("Crossings of Poros", "Minas Tirith", "yellow", 2) +
                getFormattedRoute("Crossings of Poros", "Sea of Nurnen", "violet", 4) +
                getFormattedRoute("Dagorlad (Battle Plains)", "Dol Guldur", "gray", 3) +
                getFormattedRoute("Dagorlad (Battle Plains)", "Emyn Muil", "black", 2) +
                getFormattedRoute("Dagorlad (Battle Plains)", "Emyn Muil", "orange", 2) +
                getFormattedRoute("Dagorlad (Battle Plains)", "Falls of Rauros", "white", 2) +
                getFormattedRoute("Dagorlad (Battle Plains)", "Minas Morgul", "gray", 2) +
                getFormattedRoute("Dagorlad (Battle Plains)", "Sea of Rhun", "green", 6) +
                getFormattedRoute("Dol Guldur", "East Bight", "yellow", 1) +
                getFormattedRoute("Dol Guldur", "Emyn Muil", "red", 2) +
                getFormattedRoute("Dol Guldur", "Lorien", "black", 2) +
                getFormattedRoute("East Bight", "Iron Hills", "red", 6) +
                getFormattedRoute("East Bight", "Lorien", "green", 3) +
                getFormattedRoute("East Bight", "Rivendell", "orange", 5) +
                getFormattedRoute("East Bight", "Sea of Rhun", "violet", 6) +
                getFormattedRoute("East Bight", "The Lonely Mountain", "blue", 4) +
                getFormattedRoute("Edhellond", "Erech", "green", 1) +
                getFormattedRoute("Edhellond", "Minas Tirith", "black", 4) +
                getFormattedRoute("Edhellond", "Ras Morthil", "black", 5) +
                getFormattedRoute("Edoras", "Erech", "white", 1) +
                getFormattedRoute("Edoras", "Falls of Rauros", "black", 2) +
                getFormattedRoute("Edoras", "Helm''s Deep", "gray", 1) +
                getFormattedRoute("Edoras", "Helm''s Deep", "gray", 1) +
                getFormattedRoute("Edoras", "Fangorn", "yellow", 2) +
                getFormattedRoute("Edoras", "Minas Tirith", "violet", 3) +
                getFormattedRoute("Edoras", "Minas Tirith", "red", 3) +
                getFormattedRoute("Emyn Muil", "Falls of Rauros", "gray", 1) +
                getFormattedRoute("Emyn Muil", "Falls of Rauros", "gray", 1) +
                getFormattedRoute("Emyn Muil", "Fangorn", "gray", 2) +
                getFormattedRoute("Emyn Muil", "Fangorn", "gray", 2) +
                getFormattedRoute("Erech", "Helm''s Deep", "orange", 1) +
                getFormattedRoute("Erech", "Ras Morthil", "blue", 2) +
                getFormattedRoute("Eryn Vorn", "Harlindon", "blue", 2) +
                getFormattedRoute("Eryn Vorn", "Hobbiton", "black", 3) +
                getFormattedRoute("Eryn Vorn", "Tharbad", "red", 3) +
                getFormattedRoute("Eryn Vorn", "Lond Daer", "green", 3) +
                getFormattedRoute("Ettenmoors", "Lake Evendim", "orange", 6) +
                getFormattedRoute("Ettenmoors", "Rivendell", "blue", 2) +
                getFormattedRoute("Ettenmoors", "Rivendell", "green", 2) +
                getFormattedRoute("Ettenmoors", "The Lonely Mountain", "gray", 6) +
                getFormattedRoute("Falls of Rauros", "Minas Tirith", "gray", 1) +
                getFormattedRoute("Falls of Rauros", "Minas Tirith", "gray", 1) +
                getFormattedRoute("Fangorn", "Helm''s Deep", "white", 2) +
                getFormattedRoute("Fangorn", "Isengard", "black", 2) +
                getFormattedRoute("Fangorn", "Isengard", "green", 2) +
                getFormattedRoute("Fangorn", "Lorien", "gray", 2) +
                getFormattedRoute("Fangorn", "Lorien", "gray", 2) +
                getFormattedRoute("Forlindon", "Grey Havens", "violet", 2) +
                getFormattedRoute("Forlindon", "Harlindon", "red", 4) +
                getFormattedRoute("Forlindon", "Lake Evendim", "blue", 5) +
                getFormattedRoute("Grey Havens", "Harlindon", "white", 3) +
                getFormattedRoute("Grey Havens", "Hobbiton", "green", 2) +
                getFormattedRoute("Grey Havens", "Hobbiton", "orange", 2) +
                getFormattedRoute("Grey Havens", "Lake Evendim", "gray", 3) +
                getFormattedRoute("Harlindon", "Hobbiton", "yellow", 4) +
                getFormattedRoute("Helm''s Deep", "Isengard", "gray", 2) +
                getFormattedRoute("Helm''s Deep", "Isengard", "gray", 2) +
                getFormattedRoute("Helm''s Deep", "Ras Morthil", "violet", 6) +
                getFormattedRoute("Hobbiton", "Lake Evendim", "red", 1) +
                getFormattedRoute("Hobbiton", "Lake Evendim", "violet", 1) +
                getFormattedRoute("Iron Hills", "Sea of Rhun", "yellow", 5) +
                getFormattedRoute("Iron Hills", "The Lonely Mountain", "black", 3) +
                getFormattedRoute("Isengard", "Lond Daer", "orange", 3) +
                getFormattedRoute("Isengard", "Moria''s Gate", "blue", 2) +
                getFormattedRoute("Isengard", "Moria''s Gate", "red", 2) +
                getFormattedRoute("Isengard", "Ras Morthil", "violet", 6) +
                getFormattedRoute("Isengard", "Tharbad", "gray", 3) +
                getFormattedRoute("Lond Daer", "Ras Morthil", "white", 5) +
                getFormattedRoute("Lond Daer", "Tharbad", "yellow", 3) +
                getFormattedRoute("Lorien", "Moria''s Gate", "white", 2) +
                getFormattedRoute("Lorien", "Rivendell", "gray", 2) +
                getFormattedRoute("Lorien", "Rivendell", "gray", 2) +
                getFormattedRoute("Minas Morgul", "Minas Tirith", "gray", 2) +
                getFormattedRoute("Minas Morgul", "Minas Tirith", "gray", 2) +
                getFormattedRoute("Minas Morgul", "Sea of Nurnen", "blue", 5) +
                getFormattedRoute("Moria''s Gate", "Rivendell", "violet", 2) +
                getFormattedRoute("Moria''s Gate", "Rivendell", "yellow", 2) +
                getFormattedRoute("Moria''s Gate", "Tharbad", "green", 2) +
                getFormattedRoute("Rivendell", "The Lonely Mountain", "white", 6);
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
        return String.join(",",ID,CITY_1,CITY_2,ROUTE_COLOR,ROUTE_LENGTH);
    }

    String id;
    int city1;
    int city2;
    String routeColor;
    int routeLength;
}
