package edu.goldenhammer.data_types;

import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public interface Game {
    String getID();
    String getName();
    List<String> getPlayers();
}
