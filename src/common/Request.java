package common;

import java.io.Serializable;

/**
 * Represents a command request that can be sent from client to server
 */
public class Request implements Serializable {
    private final String commandName;
    private final String[] arguments;
    private final Object data;

    public Request(String commandName, String[] arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.data = null;
    }

    public Request(String commandName, String[] arguments, Object data) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.data = data;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArguments() {
        return arguments;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", arguments=" + java.util.Arrays.toString(arguments) +
                ", data=" + data +
                '}';
    }
}