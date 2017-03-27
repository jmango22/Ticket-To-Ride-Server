package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.model.Color;
import edu.goldenhammer.model.TrainCard;
import edu.goldenhammer.server.Results;
import edu.goldenhammer.server.Serializer;

import java.util.ArrayList;

/**
 * Created by seanjib on 3/1/2017.
 */
public class DrawTrainCardCommand extends BaseCommand {
    private TrainCard card;
    private int slot;
    private Color drawnCard;
    private ArrayList<Color> bank;

    public Results execute() {
        IDatabaseController dbc = DatabaseController.getInstance();
        Results results = new Results();
        results.setResponseCode(200);
        if(slot >= 0 && slot <= 4) {
            card = TrainCard.parseDatabaseTrainCard(dbc.drawTrainCardFromSlot(getGameName(), getPlayerName(), slot));
            drawnCard = card.getColor();
            results.setMessage(Serializer.serialize(this));
        } else if(slot == 5) {
            card = TrainCard.parseDatabaseTrainCard(dbc.drawRandomTrainCard(getGameName(), getPlayerName()));
            drawnCard = card.getColor();
            results.setMessage(Serializer.serialize(this));
        } else {
            results.setResponseCode(400);
            results.setAndSerializeMessage("Error: an error occurred while drawing a card from slot " + slot);
        }
        drawnCard = card.getColor();
        return null;
    }

    public boolean validate() {
        return true;
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
}
