package edu.goldenhammer.model;


import edu.goldenhammer.database.data_types.DatabaseCity;

import java.io.Serializable;

/**
 * Created by seanjib on 3/2/2017.
 */
public class City implements Serializable{
    private double x_location;
    private double y_location;
    private String name;

    public City(double x_location, double y_location, String name) {
        this.x_location = x_location;
        this.y_location = y_location;
        this.name = name;
    }

    public static City parseDatabaseCity(DatabaseCity databaseCity) {
        double x_location = databaseCity.getPointX();
        double y_location = databaseCity.getPointY();
        String name = databaseCity.getName();
        return new City(x_location, y_location, name);
    }
}
