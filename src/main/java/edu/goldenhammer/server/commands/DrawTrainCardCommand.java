package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.data_types.DatabaseTrainCard;
import edu.goldenhammer.model.Color;
import edu.goldenhammer.model.TrainCard;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanjib on 3/1/2017.
 */
public class DrawTrainCardCommand extends BaseCommand {
    private TrainCard card;
    private int slot;
    private Color drawnCard;
    private List<Color> bank;

    public Results execute() {
        IDatabaseController dbc = DatabaseController.getInstance();
        Results results = new Results();
        results.setResponseCode(200);
        if(slot >= 0 && slot <= 4) {
            card = TrainCard.parseDatabaseTrainCard(dbc.drawTrainCardFromSlot(getGameName(), getPlayerName(), slot));
            drawnCard = card.getColor();
            bank = getSlotCards(getGameName());
            dbc.addCommand(this, true, false);
            results.setMessage(Serializer.serialize(this));
        } else if(slot == 5) {
            card = TrainCard.parseDatabaseTrainCard(dbc.drawRandomTrainCard(getGameName(), getPlayerName()));
            drawnCard = card.getColor();
            bank = getSlotCards(getGameName());
            dbc.addCommand(this, true, false);
            results.setMessage(Serializer.serialize(this));
        } else {
            results.setResponseCode(400);
            results.setAndSerializeMessage("Error: an error occurred while drawing a card from slot " + slot);
        }
        drawnCard = card.getColor();
        return null;
    }

    @Override
    public boolean endTurn() {
        if(slot >= 0 && slot <= 4 && drawnCard == Color.WILD) {
            return true;
        }
        else {
            return false;
        }
    }

    private List<Color> getSlotCards(String game_name) {
        IDatabaseController dbc = DatabaseController.getInstance();
        List<Color> slotCards = new ArrayList<>();
        List<DatabaseTrainCard> databaseTrainCards = dbc.getSlotCards(game_name);
        for(DatabaseTrainCard databaseTrainCard : databaseTrainCards) {
            slotCards.add(Color.getTrainCardColorFromString(databaseTrainCard.getTrainType()));
        }
        return slotCards;
    }
}
