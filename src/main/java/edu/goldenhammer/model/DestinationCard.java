package edu.goldenhammer.model;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.data_types.DatabaseDestinationCard;

import java.io.Serializable;

/**
 * Created by seanjib on 3/2/2017.
 */
public class DestinationCard implements Serializable{
    private City city1;
    private City city2;
    private int pointsWorth;

    public DestinationCard(City city1, City city2, int pointsWorth) {
        this.city1 = city1;
        this.city2 = city2;
        this.pointsWorth = pointsWorth;
    }

    public City getCity1() {
        return city1;
    }

    public City getCity2() {
        return city2;
    }

    public int getPointsWorth() {
        return pointsWorth;
    }

    public static DestinationCard parseDatabaseDestinationCard(DatabaseDestinationCard databaseDestinationCard) {
        IDatabaseController dbc = DatabaseController.getInstance();
        City city1 = City.parseDatabaseCity(dbc.getCity(databaseDestinationCard.getCity1()));
        City city2 = City.parseDatabaseCity(dbc.getCity(databaseDestinationCard.getCity2()));
        int pointsWorth = databaseDestinationCard.getPoints();
        return new DestinationCard(city1, city2, pointsWorth);
    }
}
