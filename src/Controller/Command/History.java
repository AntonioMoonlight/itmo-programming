package Controller.Command;

import Controller.CommandManager;
import Controller.CommandResponse;
import View.ConsoleView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class History extends Command {

    private final CommandManager commandManager;
    private final ConsoleView consoleView;

    public History(CommandManager commandManager, ConsoleView consoleView) {
        super("history", "Shows 8 last executed commands.", 0);
        this.commandManager = commandManager;
        this.consoleView = consoleView;
    }

    @Override
    public CommandResponse execute(String[] args) {
        List<String> firstEight = new ArrayList<>(8);
        Iterator<String> it = commandManager.getHistory().iterator();
        int count = 0;
        while (it.hasNext() && count < 8) {
            firstEight.add(it.next());
            count++;
        }

        consoleView.println(firstEight.toString());
        return CommandResponse.success();
    }
}
