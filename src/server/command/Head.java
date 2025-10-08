package server.command;

import server.CollectionManager;
import common.Response;
import client.ElementBuilder;

public class Head extends Command {

    private final CollectionManager collectionManager;
    
    public Head(CollectionManager collectionManager) {
        super("head", "Shows the first element of the collection", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) throws ElementBuilder.NoMoreInputException {
        var firstElement = collectionManager.getDeque().peekFirst();

        if (firstElement == null) {
            return new Response(false, "The collection is empty.");
        }

        return new Response(true, "First element:\n" + firstElement.toString());
    }
}
