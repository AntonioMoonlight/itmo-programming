package server.command;

import server.CommandManager;
import common.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class History extends Command {

    private final CommandManager commandManager;

    public History(CommandManager commandManager) {
        super("history", "Shows 8 last executed commands.", 0);
        this.commandManager = commandManager;
    }

    @Override
    public Response execute(String[] args) {
        List<String> firstEight = new ArrayList<>(8);
        Iterator<String> it = commandManager.getHistory().iterator();
        int count = 0;
        while (it.hasNext() && count < 8) {
            firstEight.add(it.next());
            count++;
        }

        if (firstEight.isEmpty()) {
            return new Response(true, "No commands in history.");
        }
        
        return new Response(true, "Command history:\n" + String.join("\n", firstEight));
    }
}
