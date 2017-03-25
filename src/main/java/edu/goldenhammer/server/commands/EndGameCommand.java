package edu.goldenhammer.server.commands;

import java.util.List;

import edu.goldenhammer.model.PlayerOverview;
import edu.goldenhammer.server.Results;

/**
 * Created by jon on 3/22/17.
 */

public class EndGameCommand extends BaseCommand {
    int winner;
    //Are these results the stats for all the players?
    // Spec: Results[] results
    List<PlayerOverview> playersResults; //get these from the database...
    
    //Results results
    public EndGameCommand() {
        setName("EndGame");
    }

    @Override
    public Results execute() {
        return null;
    }
}
