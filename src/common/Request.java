package common;

import java.io.Serializable;

public class Request implements Serializable {
    private final String commandName;
    private final Serializable args;

    public Request(String commandName, Serializable args) {
        this.commandName = commandName;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public Serializable getArgs() {
        return args;
    }
}
