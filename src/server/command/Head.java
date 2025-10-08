package server.command;

import server.CollectionManager;
import server.CommandResponse;
import client.ElementBuilder;

public class Head extends Command {

    private final CollectionManager collectionManager;
    
    public Head(CollectionManager collectionManager) {
        super("head", "Shows the first element of the collection", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        var firstElement = collectionManager.getDeque().peekFirst();

        if (firstElement == null) {
            return CommandResponse.failure("The collection is empty.");
        }

        return new CommandResponse(true, "First element:\n" + firstElement.toString());
    }
}
