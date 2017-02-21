package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/20/2017.
 */
public interface IDatabaseMessage {
    String getID();
    String getGameID();
    String getPlayerID();
    String getMessage();
}
