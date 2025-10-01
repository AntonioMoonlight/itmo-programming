package server.command;

import server.CollectionManager;
import server.CommandResponse;

public class RemoveById extends Command {
    private final CollectionManager collectionManager;
    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id", "Delete an element by id.", 1);
        this.collectionManager = collectionManager;
    }


    @Override
    public CommandResponse execute(String[] args) {
        try {
            int id = Integer.parseInt(args[0]);
             if (collectionManager.removeById(id)) {
                 return CommandResponse.success();
             } else {
                 return CommandResponse.failure("An element with given ID does not exist.");
             }
        }
        catch (NumberFormatException e) {
            return CommandResponse.failure(e.getMessage());
        }
    }

    @Override
    public String getDisplayedName() {
        return "remove_by_id id";
    }
}
