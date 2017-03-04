package edu.goldenhammer.server.commands;

import edu.goldenhammer.server.Results;

/**
 * Created by seanjib on 3/4/2017.
 */
public class LayTrackCommand extends BaseCommand {
    public LayTrackCommand() {
        setName("LayTrack");
    }

    @Override
    public Results execute() {
        return new Results();
    }
}
