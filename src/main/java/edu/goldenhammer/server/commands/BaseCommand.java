package edu.goldenhammer.server.commands;

import edu.goldenhammer.server.Results;

import java.io.Serializable;

public abstract class BaseCommand implements Serializable {
    private String name;
    private int commandNumber;

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
}
