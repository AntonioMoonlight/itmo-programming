package server.command;

import server.CollectionManager;
import server.CommandResponse;
import client.ConsoleView;

import java.time.format.DateTimeFormatter;

public class Info extends Command {

    private final ConsoleView consoleView;
    private final CollectionManager collectionManager;
    public Info(ConsoleView consoleView, CollectionManager collectionManager) {
        super("info", "Shows information about the collection.", 0);
        this.consoleView = consoleView;
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String[] args) {
        String formattedDate = collectionManager.getInitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String sb = "Information about the collection:" + "\n" +
                "Type: ArrayDeque" + "\n" +
                "Initialization date: " + formattedDate + "\n" +
                "Number of elements: " + collectionManager.getDeque().size() + "\n";
        consoleView.println(sb);
        return CommandResponse.success();
    }
}
