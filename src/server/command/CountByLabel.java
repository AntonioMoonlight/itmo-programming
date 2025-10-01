package server.command;

import server.CollectionManager;
import server.CommandResponse;
import client.ConsoleView;

public class CountByLabel extends Command {
    private final CollectionManager collectionManager;
    private final ConsoleView consoleView;
    public CountByLabel(CollectionManager collectionManager, ConsoleView consoleView) {
        super("count_by_label", "Shows the number of elements with the same label name.", 1);
        this.collectionManager = collectionManager;
        this.consoleView = consoleView;
    }

    @Override
    public CommandResponse execute(String[] args) {
        String labelName = args[0];
        consoleView.println(String.valueOf(
                collectionManager.getDeque().stream()
                .filter(b -> b.getLabel().getName().equals(labelName))
                .count()));
        return CommandResponse.success();
    }

    @Override
    public String getDisplayedName() {
        return "count_by_label label";
    }
}
