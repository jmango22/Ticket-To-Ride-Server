package edu.goldenhammer.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by seanjib on 3/2/2017.
 */
public class Hand implements Serializable{
    private List<DestinationCard> destinationCards;
    private List<TrainCard> trainCards;
    private DrawnDestinationCards drawnDestinationCards;

    public Hand(List<DestinationCard> destinationCards, List<TrainCard> trainCards, DrawnDestinationCards drawnDestinationCards) {
        this.destinationCards = destinationCards;
        this.trainCards = trainCards;
        this.drawnDestinationCards = drawnDestinationCards;
    }
}
