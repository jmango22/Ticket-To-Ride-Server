package edu.goldenhammer.model;

/**
 * Created by seanjib on 3/2/2017.
 */
public class Track {
    private City city1;
    private City city2;
    private int length;
    private Color color;
    private int owner;
    private double location1x;
    private double location1y;
    private double location2x;
    private double location2y;

    public Track(City city1, City city2, int length, Color color, int owner, double location1x, double location1y, double location2x, double location2y) {
        this.city1 = city1;
        this.city2 = city2;
        this.length = length;
        this.color = color;
        this.owner = owner;
        this.location1x = location1x;
        this.location1y = location1y;
        this.location2x = location2x;
        this.location2y = location2y;
    }
}
