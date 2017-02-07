package edu.goldenhammer.commands;

import edu.goldenhammer.server.Results;

import java.io.Serializable;

public abstract class BaseCommand implements Serializable {
    private String _name;

    public String getName() {
        return _name;
    }

    protected final void setName(String name) {
        _name = name;
    }

    public abstract Results execute();
}
