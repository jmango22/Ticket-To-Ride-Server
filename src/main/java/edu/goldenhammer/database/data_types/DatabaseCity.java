package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseCity implements Serializable, IDatabaseCity{
    public static final String ID = "city_id";
    public static final String NAME = "city_name";
    public static final String TABLE_NAME = "city";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL INTEGER NOT NULL," +
                    "%3$s VARCHAR(20)," +
                    "PRIMARY KEY(%4$s)" +
                    ");",
            TABLE_NAME,
            ID,
            NAME,
            ID);

    public DatabaseCity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static String columnNames() {
        return String.join(",", ID, NAME);
    }

    private String id;
    private String name;
}
