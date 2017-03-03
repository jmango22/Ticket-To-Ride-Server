package edu.goldenhammer.model;

/**
 * Created by seanjib on 3/2/2017.
 */
public class DestinationCard {
    private City city1;
    private City city2;
    private int pointsWorth;

    public DestinationCard(City city1, City city2, int pointsWorth) {
        this.city1 = city1;
        this.city2 = city2;
        this.pointsWorth = pointsWorth;
    }
}
