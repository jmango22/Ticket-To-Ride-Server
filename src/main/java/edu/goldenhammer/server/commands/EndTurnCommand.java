package edu.goldenhammer.server.commands;

import edu.goldenhammer.database.DatabaseController;
import edu.goldenhammer.database.IDatabaseController;
import edu.goldenhammer.server.Results;

/**
 * Created by seanjib on 3/4/2017.
 */
public class EndTurnCommand extends BaseCommand {
    int previousPlayer;
    int nextPlayer;
    public EndTurnCommand() {
        setName("EndTurn");
    }

    @Override
    public Results execute() {
        addToDatabase();
        return new Results();
    }

    public int getPreviousPlayer() {
        return previousPlayer;
    }

    public void setPreviousPlayer(int previousPlayer) {
        this.previousPlayer = previousPlayer;
    }

    public int getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(int nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    protected void addToDatabase() {
        super.addToDatabase(true, true);
    }
}
