package edu.goldenhammer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.data_types.DatabaseDestinationCard;
import edu.goldenhammer.database.data_types.DatabasePlayer;

/**
 * Created by jon on 3/24/17.
 */

public class ResultsGenerator {
    private List<EndResult> endResults;

    public ResultsGenerator() {
        endResults = new ArrayList<>();
    }

    public ResultsGenerator(String game_name) {
        endResults = new ArrayList<>();
        generateResults(game_name);
    }

    public List<EndResult> generateResults(String game_name) {
        //Use the Database to grab the right game
        GameModel endModel = (GameModel)DatabaseController.getInstance().getGameModel(game_name);
        //From the GameModel get the Player Data
        List<PlayerOverview> players = endModel.getPlayers();
        //From the GameModel get the Map Data
        Map endMap = endModel.getMap();

        //All the Player Final Results
        List<EndResult> results = new ArrayList<>();

        for(PlayerOverview player : players) {
            int playerId = 0;
            int builtTrainPoints = 0;
            int completedDestinations = 0;
            int incompleteDestinations = 0; //Allows a negative number
            int longestContinuousTrain = 0;
            int total = 0;

            //set the right playerId
            playerId = player.getPlayer();

            //built Train points should be the same as the LeaderBoard Points...
            builtTrainPoints = player.getPoints();

            //Get the TrackForest Object
            TrackForest trackForest = new TrackForest(endMap.getTracks());
            Set<Integer> playersWithLongestTrack = trackForest.getPlayerWithLongestTrack();

            //get the player's Destination Cards
            List<DatabaseDestinationCard> playerCards = DatabaseController.getInstance().getPlayerDestinationCards(endModel.getName().toString(), player.getPlayer());

            //Go through the player's Destination Cards and see if they have completed it
            for(DatabaseDestinationCard databaseCard : playerCards) {
                //TODO : use the TrackForest to see if the destination complete or not.
                DestinationCard card = DestinationCard.parseDatabaseDestinationCard(databaseCard);
                if(trackForest.connectedCities(card.getCity1(), card.getCity2(), player.getPlayer())) {
                    completedDestinations = completedDestinations + card.getPointsWorth();
                } else {
                    incompleteDestinations = incompleteDestinations - card.getPointsWorth();
                }
                if(playersWithLongestTrack.contains(player.getPlayer())) {
                    longestContinuousTrain = longestContinuousTrain + 10;
                }

            }

            total = builtTrainPoints + completedDestinations + incompleteDestinations + longestContinuousTrain;

            EndResult currentPlayerResults = new EndResult(playerId, builtTrainPoints, completedDestinations, incompleteDestinations, longestContinuousTrain, total);
            results.add(currentPlayerResults);
        }
        return results;
    }
}
