package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import View.ConsoleView;

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

        StringBuilder sb = new StringBuilder(
                "Information about the collection:").append("\n")
                .append("Type: ArrayDeque").append("\n")
                .append("Initialization date: ").append(formattedDate).append("\n")
                .append("Number of elements: ").append(collectionManager.getDeque().size()).append("\n");
        consoleView.println(sb.toString());
        return CommandResponse.success();
    }
}
