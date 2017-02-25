package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseCity implements Serializable, IDatabaseCity{
    public static final String ID = "city_id";
    public static final String NAME = "city_name";
    public static final String POINT_X = "point_x";
    public static final String POINT_Y = "point_y";
    public static final String TABLE_NAME = "city";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL NOT NULL," +
                    "%3$s VARCHAR(20) UNIQUE," +
                    "%4$s DECIMAL NOT NULL," +
                    "%5$s DECIMAL NOT NULL," +
                    "PRIMARY KEY(%2$s)" +
                    ");" +
                    "INSERT INTO %1$s(%3$s, %4$s, %5$s) VALUES %6$s",
            TABLE_NAME,
            ID,
            NAME,
            POINT_X,
            POINT_Y,
            getAllCities()
            );

    public DatabaseCity(String id, String name, double pointX, double pointY) {
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
    public double getPointX() {
        return pointX;
    }

    @Override
    public double getPointY() {
        return pointY;
    }

    public static String columnNames() {
        return String.join(",", ID, NAME);
    }

    public static String getAllCities() {
        String formattedCities =
                getFormattedCity("Amon Sul", 0, 0) +
                getFormattedCity("Ash Mountains", 0, 0) +
                getFormattedCity("Barad Dur", 0, 0) +
                getFormattedCity("Bree", 0, 0) +
                getFormattedCity("Crossings of Poros", 0, 0) +
                getFormattedCity("Dagorlad (Battle Plains)", 0, 0) +
                getFormattedCity("Dol Guldur", 0, 0) +
                getFormattedCity("East Bight", 0, 0) +
                getFormattedCity("Edhellond", 0, 0) +
                getFormattedCity("Edoras", 0, 0) +
                getFormattedCity("Emyn Muil", 0, 0) +
                getFormattedCity("Erech", 0, 0) +
                getFormattedCity("Eryn Vorn", 0, 0) +
                getFormattedCity("Ettenmoors", 0, 0) +
                getFormattedCity("Falls of Rauros", 0, 0) +
                getFormattedCity("Fangorn", 0, 0) +
                getFormattedCity("Forlindon", 0, 0) +
                getFormattedCity("Grey Havens", 0, 0) +
                getFormattedCity("Harlindon", 0, 0) +
                getFormattedCity("Helm's Deep", 0, 0) +
                getFormattedCity("Hobbiton", 0, 0) +
                getFormattedCity("Iron Hills", 0, 0) +
                getFormattedCity("Isengard", 0, 0) +
                getFormattedCity("Lake Evendim", 0, 0) +
                getFormattedCity("Lond Daer", 0, 0) +
                getFormattedCity("Lorien", 0, 0) +
                getFormattedCity("Minas Morgul", 0, 0) +
                getFormattedCity("Minas Tirith", 0, 0) +
                getFormattedCity("Moria's Gate", 0, 0) +
                getFormattedCity("Ras Morthil", 0, 0) +
                getFormattedCity("Rivendell", 0, 0) +
                getFormattedCity("Sea of Nurnen", 0, 0) +
                getFormattedCity("Sea of Rhun", 0, 0) +
                getFormattedCity("Tharbad", 0, 0) +
                getFormattedCity("The Lonely Mountain", 0, 0);
        return formattedCities.substring(0, formattedCities.length() - 1) + ';'; //replaces the final comma with a semicolon
    }

    public static String getFormattedCity(String cityName, double pointX, double pointY) {
        return String.format("('%1', %2, %3),",
                cityName,
                pointX,
                pointY);
    }

    private String id;
    private String name;
    private double pointX;
    private double pointY;
}
