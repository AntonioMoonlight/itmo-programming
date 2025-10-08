package server.command;

import server.CollectionManager;
import common.Response;

public class RemoveById extends Command {
    private final CollectionManager collectionManager;
    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id", "Delete an element by id.", 1);
        this.collectionManager = collectionManager;
    }


    @Override
    public Response execute(String[] args) {
        try {
            int id = Integer.parseInt(args[0]);
             if (collectionManager.removeById(id)) {
                 return new Response(true, "Element with ID " + id + " removed successfully.");
             } else {
                 return new Response(false, "An element with given ID does not exist.");
             }
        }
        catch (NumberFormatException e) {
            return new Response(false, e.getMessage());
        }
    }

    @Override
    public String getDisplayedName() {
        return "remove_by_id id";
    }
}
