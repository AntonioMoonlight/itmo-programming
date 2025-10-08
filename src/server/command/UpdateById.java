package server.command;

import server.CollectionManager;
import server.CommandResponse;

public class UpdateById extends Command {

    public UpdateById(CollectionManager collectionManager) {
        super("update_by_id", "Updates the element with given ID from user input", 1);
    }

    @Override
    public CommandResponse execute(String[] args) {
        // This method should not be called directly in client-server mode
        // The RequestProcessor handles update_by_id with data
        return CommandResponse.failure("This command requires object data and should be handled by RequestProcessor");
    }

    @Override
    public String getDisplayedName() {
        return "update_by_id id";
    }
}
