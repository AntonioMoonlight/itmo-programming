package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import Controller.ElementBuilder;
import  Model.MusicBand;
import View.ConsoleView;

import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Head extends Command {

    private final CollectionManager collectionManager;
    private final ConsoleView consoleView;
    public Head(CollectionManager collectionManager, ConsoleView consoleView) {
        super("head", "Shows the first element of the collection", 0);
        this.collectionManager = collectionManager;
        this.consoleView = consoleView;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        if (collectionManager.getDeque().isEmpty()) {
            return CommandResponse.failure("The collection is empty.");
        }

        consoleView.printMusicBandTable(collectionManager.getDeque().peekFirst());
        return CommandResponse.success();
    }
}
