package edu.goldenhammer.model;

import java.util.List;

/**
 * Created by devonkinghorn on 2/8/17.
 */
public interface IGame {
    String getID();
    String getName();
    List<String> getPlayers();
    void setPlayers(List<String> players);
}
