package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import Controller.ElementBuilder;

public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", "Removes all elements from the collection.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String[] args) {
        collectionManager.clear();
        return CommandResponse.success();
    }
}
