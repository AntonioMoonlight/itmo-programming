package server.command;

import server.CollectionManager;
import common.Response;

public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "Shows all elements of the collection.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        if (collectionManager.getDeque().isEmpty()) {
            return new Response(true, "Collection is empty.");
        }
        return new Response(true, "Collection contents:");
    }
}
