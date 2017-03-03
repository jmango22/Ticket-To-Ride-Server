package edu.goldenhammer.model;

import java.util.List;

/**
 * Created by seanjib on 3/2/2017.
 */
public class Map {
    private List<Track> tracks;
    private List<City> cities;

    public Map(List<Track> tracks, List<City> cities) {
        this.tracks = tracks;
        this.cities = cities;
    }
}
