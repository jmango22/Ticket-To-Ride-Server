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
        if(dbc.claimRoute(getGameName(), getPlayerName(), track.getRoute_number())) {
            addToDatabase(true, true);
            for(Color card : cards) {
                dbc.discardCard(getGameName(),getPlayerName(),card);
            }
        }
        return new Results();
    }

    public boolean validate() {
        DatabaseController dbc = DatabaseController.getInstance();
        if(track.getColor() != null) {
            for (Color color : cards) {
                if (color != Color.WILD && color != track.getColor()) {
                    return false;
                }
            }
        }
        if(cards.length != track.getLength())
            return false;
//TODO: finish validating the cards
        List<Track> tracks = dbc.getTracks(getGameName());
        if(track.getColor() == null)
            track.setColor(Color.GRAY);
        for(Track _track: tracks) {
            if(_track.getRoute_number() == track.getRoute_number()) {
                return (track.getColor() == _track.getColor()
                        && track.getLength() == _track.getLength()
                        && _track.getOwner() == -1);
            }
        }
        return false;
    }

    @Override
    public boolean isLastRound() {
        return DatabaseController.getInstance().numTrainsLeft(getGameName(),getPlayerName()) <= 2;
    }
}
