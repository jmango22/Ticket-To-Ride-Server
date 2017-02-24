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
                    "    %3$s VARCHAR(20) NOT NULL,\n" +
                    "    %4$s VARCHAR(20) NOT NULL,\n" +
                    "    %5$s VARCHAR(10) NOT NULL,\n" +
                    "    %6$s INTEGER NOT NULL,\n" +
                    "    PRIMARY KEY(2$s)\n" +
                    ");" +
                    "INSERT INTO %1$s(%3$s, %4$s, %5$s, %6$s) VALUES" +
                    "('The Shire', 'Lothlorien', 'red', 3)," +
                    "('Rivindell', 'The Shire', 'gray', 5)," +
                    "('Forodwaith', 'Mordor', 'white', 4)" +
                    ";", //City 1, City 2, Color, Length
            TABLE_NAME,
            ID,
            CITY_1,
            CITY_2,
            ROUTE_COLOR,
            ROUTE_LENGTH);

    public DatabaseRoute(String id, String city1, String city2, String routeColor, int routeLength) {
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
    public String getCity1() {
        return city1;
    }

    @Override
    public String getCity2() {
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

    public static String columnNames() {
        return String.join(",",ID,CITY_1,CITY_2,ROUTE_COLOR,ROUTE_LENGTH);
    }

    String id;
    String city1;
    String city2;
    String routeColor;
    int routeLength;
}
