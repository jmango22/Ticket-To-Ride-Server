package edu.goldenhammer.server;

import java.util.ArrayList;
import java.util.List;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.Lock;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.commands.EndTurnCommand;

/**
 * Created by root on 3/15/17.
 *
 * Check command type
 * Execute all the commands and put them into the database, and execute them.
 */

public class CommandManager {

    /**
     * Add the basecommand, and then validate and execute
     * Check command type,
     * @param command
     * @return all the commands, such as base command, and endturn, null if not allowed
     */

    public Results addCommand(BaseCommand command) {
        synchronized (Lock.getInstance().getLock(command.getGameName())) {
            List<Results> executed = new ArrayList<>();
            Results result = new Results();

            if(currentPlayerTurn(command.getPlayerNumber(), command.getGameName())) {
                if (command.validate()) {
                    result = command.execute();
                    if (command.endTurn()) {
                        EndTurnCommand endTurn = new EndTurnCommand();
                        endTurn.setGameName(command.getGameName());
                        endTurn.setCommandNumber(command.getCommandNumber()+1);
                        endTurn.setPlayerNumber(command.getPlayerNumber());
                        endTurn.execute();
                    }
                }
            }
            return result;
        }
    }

    //Tests that it's the current players turn.
    //Check the last EndTurnCommands player number and see what player is next.
    private boolean currentPlayerTurn(int playerNumber, String game_name) {
        DatabaseController dbc = DatabaseController.getInstance();
        //This doesn't work yet... The getCurrentPlayerTurn doesn't return a real player number yet. It needs to be written.
        if(dbc.getCurrentPlayerTurn(game_name) == playerNumber) {
            return true;
        }
        return false;
    }
}
