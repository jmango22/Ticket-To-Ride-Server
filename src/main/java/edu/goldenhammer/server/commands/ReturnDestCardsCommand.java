package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.data_types.DatabaseDestinationCard;
import edu.goldenhammer.model.DestinationCard;
import edu.goldenhammer.model.Hand;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.util.ArrayList;
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
        IDatabaseController dbc = DatabaseController.getInstance();
        Results results = new Results();
        try {
            if(dbc.returnDestCards(getGameName(), getPlayerName(), toReturn)) {
                results.setResponseCode(200);
                Serializer serializer = new Serializer();
                results.setMessage(serializer.serialize(this));
                addToDatabase(true, false);
            }
            else {
                results.setResponseCode(400);
                results.setAndSerializeMessage("Error: You cannot return those cards");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            results.setResponseCode(400);
            results.setAndSerializeMessage("Error: Invalid input");
        }
        return results;
    }
}
