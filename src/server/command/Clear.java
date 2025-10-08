package server.command;

import server.CollectionManager;
import common.Response;

public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", "Removes all elements from the collection.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        collectionManager.clear();
        return new Response(true, "Collection cleared.");
    }
}
