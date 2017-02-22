package edu.goldenhammer.database.data_types;

import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public interface IDatabaseParticipants {
    String getPlayerID();
    String getGameID();
    int getPlayerNumber();
    int getPoints();
    int getTrainsLeft();
}
