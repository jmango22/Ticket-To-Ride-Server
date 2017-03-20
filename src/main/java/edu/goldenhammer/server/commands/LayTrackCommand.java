package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.model.Color;
import edu.goldenhammer.model.Track;
import edu.goldenhammer.server.Results;

/**
 * Created by seanjib on 3/4/2017.
 */
public class LayTrackCommand extends BaseCommand {
    Color[] cards;
    Track track;

    public LayTrackCommand() {
        setName("LayTrack");
    }

    @Override
    public Results execute() {
        DatabaseController dbc = DatabaseController.getInstance();
        for(Color card : cards) {
            dbc.discardCard(getGameName(),getPlayerName(),card);
        }

        dbc.claimRoute(getGameName(), getPlayerName(), track.getRoute_number());
        return new Results();
    }

    public boolean validate() {
//        make sure the player has the right card
//        Make sure the track is open
        return true;
    }
}
