package edu.goldenhammer.model;

import java.util.List;

/**
 * Created by devonkinghorn on 2/8/17.
 */
public interface IGameOverview {
    String getID();
    String getName();
    boolean isStarted();
    List<String> getPlayers();
    void setPlayers(List<String> players);
}
