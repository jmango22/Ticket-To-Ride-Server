package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.data_types.DatabaseDestinationCard;
import edu.goldenhammer.model.DestinationCard;
import edu.goldenhammer.model.DrawnDestinationCards;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanjib on 3/4/2017.
 */
public class DrawDestCardsCommand extends BaseCommand {
    private List<DestinationCard> cards;

    {
        cards = new ArrayList<>();
    }

    @Override
    public Results execute() {
        Results results = new Results();
        cards = getDestinationCards();
        if(cards.size() == 3) {
            results.setResponseCode(200);
            results.setMessage(Serializer.serialize(this));
            addToDatabase(true,false);
        }
        else {
            results.setResponseCode(400);
            results.setAndSerializeMessage("Error: failed to draw cards");
        }
        return results;
    }

    private List<DestinationCard> getDestinationCards() {
        IDatabaseController dbc = DatabaseController.getInstance();
//        List<DatabaseDestinationCard> databaseCards = dbc.drawDestinationCards(getGameName(), getPlayerName(), getCommandNumber(), getPlayerNumber());
//        List<DestinationCard> cards = new ArrayList<>();
//
//        for(DatabaseDestinationCard databaseCard : databaseCards) {
//            cards.add(DestinationCard.parseDatabaseDestinationCard(databaseCard));
//        }
        List<DestinationCard> cards = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            DestinationCard destinationCard = DestinationCard.parseDatabaseDestinationCard(dbc.drawRandomDestinationCard(getGameName(), getPlayerName()));
            cards.add(destinationCard);
        }
        return cards;
    }

    @Override
    public boolean endTurn() {
        return false;
    }
}
