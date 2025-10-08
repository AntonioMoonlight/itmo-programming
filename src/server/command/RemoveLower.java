package server.command;

import server.CollectionManager;
import server.CommandResponse;

public class RemoveLower extends Command {

    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "Removes all element less than the given one.", 0);
    }

    @Override
    public CommandResponse execute(String[] args) {
        // This method should not be called directly in client-server mode
        // The RequestProcessor handles remove_lower with data
        return CommandResponse.failure("This command requires object data and should be handled by RequestProcessor");
    }

    @Override
    public String getDisplayedName() {
        return "remove_lower {element}";
    }
}
