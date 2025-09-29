package common;

import java.io.Serializable;

public class Request implements Serializable {
    private final String commandName;
    private final Serializable argument;

    public Request(String commandName, Serializable argument) {
        this.commandName = commandName;
        this.argument = argument;
    }

    public String getCommandName() {
        return commandName;
    }

    public Serializable getArgument() {
        return argument;
    }
}
