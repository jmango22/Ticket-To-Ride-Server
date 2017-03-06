package edu.goldenhammer.server.commands;

import edu.goldenhammer.model.DestinationCard;
import edu.goldenhammer.server.Results;

import java.util.List;

/**
 * Created by seanjib on 3/4/2017.
 */
public class ReturnDestCardsCommand extends BaseCommand {
    List<DestinationCard> toReturn;

    public ReturnDestCardsCommand() {
        setName("ReturnDestCards");
    }

    public Results execute() {
        return new Results();
    }
}
