package edu.goldenhammer.server.commands;

import edu.goldenhammer.server.Results;

/**
 * Created by seanjib on 3/4/2017.
 */
public class InitializeHandCommand extends BaseCommand {
    public InitializeHandCommand() {
        setName("InitializeHand");
    }

    public Results execute() {
        return new Results();
    }
}
