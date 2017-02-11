package edu.goldenhammer.database.data_types;

import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public interface IParticipants {
    String getID();
    String getName();
    boolean isStarted();
    List<String> getPlayers();
    void setPlayers(List<String> players);
}
