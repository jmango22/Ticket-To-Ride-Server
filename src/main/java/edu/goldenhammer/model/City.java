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

    public String getName() {
        return name;
    }

    public City(double x_location, double y_location, String name) {
        this.x_location = x_location;
        this.y_location = y_location;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        } else if(!(o instanceof City)) {
            return false;
        } else {
            if(((City) o).getName().equals(getName())) {
                return true;
            }
            return false;
        }
    }
}
