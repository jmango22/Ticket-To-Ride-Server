package edu.goldenhammer.model;

import java.util.ArrayList;
import java.util.List;

import edu.goldenhammer.database.DatabaseController;
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

            //get more detailed Player information from the player's hand
            //DatabasePlayer detailedPlayer = (DatabasePlayer)DatabaseController.getInstance().
        }
        return new ArrayList<>();
    }
}
