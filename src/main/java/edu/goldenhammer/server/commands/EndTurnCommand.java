package edu.goldenhammer.server.commands;

import edu.goldenhammer.server.Results;

/**
 * Created by seanjib on 3/4/2017.
 */
public class EndTurnCommand extends BaseCommand {
    public EndTurnCommand() {
        setName("EndTurn");
    }

    @Override
    public Results execute() {
        return new Results();
    }
}
