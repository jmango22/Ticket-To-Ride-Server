package edu.goldenhammer.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.database.Lock;
import edu.goldenhammer.server.commands.BaseCommand;
import edu.goldenhammer.server.commands.EndGameCommand;
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

    public List<BaseCommand> addCommand(BaseCommand command) {
        synchronized (Lock.getInstance().getLock(command.getGameName())) {
            List<BaseCommand> executed = new ArrayList<>();

            if(currentPlayerTurn(command.getPlayerNumber(), command.getGameName())) {
                if (command.validate()) {
                    command.execute();
                    executed.add(command);
                    if(command.isEndOfGame()) {

                    }
                    else if (command.endTurn()) {
                        EndTurnCommand endTurn = DatabaseController.getInstance().getEndTurnCommand(command.getGameName(), command.getCommandNumber()+1, command.getPlayerName());
                        endTurn.execute();
                        executed.add(endTurn);
                    }
                }
            }
            return executed;
        }
    }

    private boolean currentPlayerTurn(int playerNumber, String game_name) {
        DatabaseController dbc = DatabaseController.getInstance();
        if(dbc.getCurrentPlayerTurn(game_name) == playerNumber) {
            return true;
        }
        return false;
    }
}
