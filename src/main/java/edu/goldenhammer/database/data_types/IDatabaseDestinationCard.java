package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/20/2017.
 */
public interface IDatabaseDestinationCard {
    String getID();
    String getGameID();
    int getCity1();
    int getCity2();
    String getPlayerID();
    boolean isDiscarded();
    int getPoints();
    boolean isDrawn();
}