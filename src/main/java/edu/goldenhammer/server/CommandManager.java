package edu.goldenhammer.server;

import java.util.ArrayList;
import java.util.List;

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

    /*
    public CommandManager(BaseCommand baseCommand) {
        this.baseCommand = baseCommand;
    }
    */

    /**
     * Add the basecommand, and then validate and execute
     * Check command type,
     * @param command
     * @return all the commands, such as base command, and endturn, null if not allowed
     */

    //Add the basecommand, and then validate and execute
    //Returns all the
    public List<BaseCommand> addCommand(BaseCommand command) {
        synchronized (Lock.getInstance().getLock(command.getGameName())) {
            List<BaseCommand> executed = new ArrayList<>();

            if(currentPlayerTurn(command.getPlayerName())) {
                if (command.validate()) {
                    command.execute();
                    executed.add(command);
                    if (command.endTurn()) {
                        EndTurnCommand endTurn = new EndTurnCommand();
                        endTurn.setGameName(command.getGameName());
                        endTurn.setCommandNumber(command.getCommandNumber()+1);
                        endTurn.setPlayerNumber(command.getPlayerNumber());
                        endTurn.execute();
                        executed.add(endTurn);
                    }
                }
            }
            return executed;
        }
    }

    //Tests that it's the current players turn.
    //Check the last EndTurnCommands player number and see what player is next.
    private boolean currentPlayerTurn(String player_name) {
        return true;
    }
}
