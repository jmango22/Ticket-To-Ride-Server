package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseCity implements Serializable, IDatabaseCity{
    public static final String CITY_FILE_PATH = System.getProperty("user.dir") + "/src/main/res/cities.txt";
    public static final int CITY_COUNT = 35;
    public static final String ID = "city_id";
    public static final String NAME = "city_name";
    public static final String POINT_X = "point_x";
    public static final String POINT_Y = "point_y";
    public static final String TABLE_NAME = "city";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL NOT NULL," +
                    "%3$s VARCHAR(30) UNIQUE," +
                    "%4$s INTEGER NOT NULL," +
                    "%5$s INTEGER NOT NULL," +
                    "PRIMARY KEY(%2$s)" +
                    ");",
            TABLE_NAME,
            ID,
            NAME,
            POINT_X,
            POINT_Y
            );
    public static final String INSERT_STMT = String.format(
            "INSERT INTO %1$s(%2$s, %3$s, %4$s) VALUES %5$s",
            TABLE_NAME,
            NAME,
            POINT_X,
            POINT_Y,
            getAllCities()
    );

    public DatabaseCity(String id, String name, int pointX, int pointY) {
        this.id = id;
        this.name = name;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPointX() {
        return pointX;
    }

    @Override
    public int getPointY() {
        return pointY;
    }

    public static String columnNames() {
        return String.join(",", ID, NAME);
    }

    public static String getAllCities() {
        String formattedCities = "";
        for(int i = 0; i < CITY_COUNT; i++) {
            formattedCities += getFormattedCity();
        }
        return formattedCities.substring(0, formattedCities.length() - 2) + ";"; //replaces the final comma with a semicolon
    }

    public static String getFormattedCity() {
        return "(?, ?, ?),\n";
    }

    private String id;
    private String name;
    private int pointX;
    private int pointY;
}
