package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseRoute implements IDatabaseRoute {
    public static final String ID = "route_id";
    public static final String CITY_1 = "city_1";
    public static final String CITY_2 = "city_2";
    public static final String ROUTE_COLOR = "route_color";
    public static final String ROUTE_LENGTH = "route_length";
    public static final String TABLE_NAME = "route";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE if not exists %1$s (\n" +
                    "    2$s SERIAL INTEGER NOT NULL,\n" +
                    "    3$s UNIQUE INTEGER NOT NULL,\n" +
                    "    4$s UNIQUE INTEGER NOT NULL,\n" +
                    "    5$s VARCHAR(10) NOT NULL,\n" +
                    "    6$s INTEGER NOT NULL,\n" +
                    "    PRIMARY KEY 2$s,\n" +
                    "    FOREIGN KEY(3$s)\n" +
                    "      REFERENCES 7$s\n" +
                    "      ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY(4$s)\n" +
                    "      REFERENCES 7$s\n" +
                    "      ON DELETE CASCADE)",
            TABLE_NAME,
            ID,
            CITY_1,
            CITY_2,
            ROUTE_COLOR,
            ROUTE_LENGTH,
            DatabaseCity.TABLE_NAME);

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
