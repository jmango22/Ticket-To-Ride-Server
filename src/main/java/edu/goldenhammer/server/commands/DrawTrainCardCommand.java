package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.model.Color;
import edu.goldenhammer.model.TrainCard;
import edu.goldenhammer.server.Results;

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
        DatabaseController dbc = DatabaseController.getInstance();
        if(slot == -1) {
            card = TrainCard.parseDatabaseTrainCard(dbc.drawRandomTrainCard(getGameName(), getPlayerName()));
        } else {
            //todo:
        }
        drawnCard = card.getColor();
        return null;
    }

    public boolean validate() {

        return true;
    }

}
