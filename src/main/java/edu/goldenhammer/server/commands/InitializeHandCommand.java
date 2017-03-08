package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.data_types.DatabaseTrainCard;
import edu.goldenhammer.model.DestinationCard;
import edu.goldenhammer.model.DrawnDestinationCards;
import edu.goldenhammer.model.Hand;
import edu.goldenhammer.model.TrainCard;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanjib on 3/4/2017.
 */
public class InitializeHandCommand extends BaseCommand {

    private Hand hand;

    public InitializeHandCommand() {
        setName("InitializeHand");
    }

    public Results execute() {
        IDatabaseController dbc = DatabaseController.getInstance();

        Results results = new Results();
        hand = null;
        try {
            hand = new Hand(new ArrayList<>(), drawTrainCards(dbc), drawDestinationCards(dbc));
            results.setResponseCode(200);
        } catch (Exception ex) {
            ex.printStackTrace();
            results.setResponseCode(400);
        }
        Serializer serializer = new Serializer();
        results.setMessage(serializer.serialize(this));
        return results;
    }

    private List<TrainCard> drawTrainCards(IDatabaseController dbc) {
        List<TrainCard> trainCards = new ArrayList<>();
        for(int i = 0; i < DatabaseTrainCard.MAX_STARTING_CARDS; i++) {
            TrainCard trainCard = TrainCard.parseDatabaseTrainCard(dbc.drawRandomTrainCard(getGameName(), getPlayerName()));
            trainCards.add(trainCard);
        }

        return trainCards;
    }

    private DrawnDestinationCards drawDestinationCards(IDatabaseController dbc) {
        List<DestinationCard> cards = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            DestinationCard destinationCard = DestinationCard.parseDatabaseDestinationCard(dbc.drawRandomDestinationCard(getGameName(), getPlayerName()));
            cards.add(destinationCard);
        }
        return new DrawnDestinationCards(cards);
    }
}
