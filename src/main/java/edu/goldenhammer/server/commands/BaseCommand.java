package edu.goldenhammer.server.commands;

import edu.goldenhammer.server.Results;

import java.io.Serializable;

public abstract class BaseCommand implements Serializable {
    private String name;
    private int playerNumber;
    private int commandNumber;
    private String gameName;
    private String playerName;

    public String getName() {
        return name;
    }

    public int getCommandNumber() {
        return commandNumber;
    }

    protected final void setName(String name) {
        this.name = name;
    }

    protected final void setCommandNumber(int commandNumber) {
        this.commandNumber = commandNumber;
    }

    public abstract Results execute();

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
