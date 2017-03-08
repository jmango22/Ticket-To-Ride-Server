package edu.goldenhammer.model;

import edu.goldenhammer.database.data_types.DatabaseTrainCard;

import java.io.Serializable;

/**
 * Created by seanjib on 3/2/2017.
 */
public class TrainCard implements Serializable{
    private Color color;

    public TrainCard(Color color) {
        this.color = color;
    }

    public static TrainCard parseDatabaseTrainCard(DatabaseTrainCard databaseTrainCard) {
        Color color = Color.getTrainCardColorFromString(databaseTrainCard.getTrainType());
        return new TrainCard(color);
    }
}
