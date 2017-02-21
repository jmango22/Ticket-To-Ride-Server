package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public interface IDatabaseTrainCard extends Serializable {
    String getID();
    String getGameID();
    String getPlayerID();
    String getTrainType();
    boolean isDiscarded();
}
