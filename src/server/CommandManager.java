package server;
import server.command.Command;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> registry = new HashMap<>();
    private final ArrayDeque<String> history = new ArrayDeque<>();


    public void register(Command cmd) { registry.put(cmd.getName(), cmd); }

    public Map<String, Command> getRegistry() {
        return registry;
    }

    public ArrayDeque<String> getHistory() {
        return history;
    }

}
