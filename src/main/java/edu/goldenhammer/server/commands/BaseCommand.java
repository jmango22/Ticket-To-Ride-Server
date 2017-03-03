package edu.goldenhammer.server.commands;

import edu.goldenhammer.server.Results;

import java.io.Serializable;

public abstract class BaseCommand implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    protected final void setName(String name) {
        this.name = name;
    }

    public abstract Results execute();
}
