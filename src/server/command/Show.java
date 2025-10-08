package server.command;

import server.CollectionManager;
import server.CommandResponse;

public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "Shows all elements of the collection.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String[] args) {
        if (collectionManager.getDeque().isEmpty()) {
            return new CommandResponse(true, "Collection is empty.");
        }
        return new CommandResponse(true, "Collection contents:");
    }
}
