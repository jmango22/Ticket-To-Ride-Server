package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.model.*;
import edu.goldenhammer.server.Results;

import java.util.List;

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
        DatabaseController dbc = DatabaseController.getInstance();
        for(Color color: cards) {
            if(color != Color.WILD && color != track.getColor()) {
                return false;
            }
        }
        if(cards.length != track.getLength())
            return false;

        List<Track> tracks = dbc.getTracks(getGameName());

        for(Track _track: tracks) {
            if(_track.getRoute_number() == track.getRoute_number()) {
                return (track.getColor() == _track.getColor()
                        && track.getLength() == _track.getLength()
                        && _track.getOwner() == -1);
            }
        }
        return false;
    }
}
