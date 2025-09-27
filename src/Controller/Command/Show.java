package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import View.ConsoleView;

public class Show extends Command {
    private final ConsoleView consoleView;
    private final CollectionManager collectionManager;

    public Show(ConsoleView consoleView, CollectionManager collectionManager) {
        super("show", "Shows all elements of the collection.", 0);
        this.consoleView = consoleView;
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String[] args) {
        consoleView.printMusicBandsTable(collectionManager.getDeque());

        return CommandResponse.success();
    }
}
