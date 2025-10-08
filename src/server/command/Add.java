package server.command;

import server.CollectionManager;
import server.CommandResponse;

public class Add extends Command {

    public Add(CollectionManager collectionManager) {
        super("add", "Adds a new element to the collection from user input.", 0);
    }

    @Override
    public CommandResponse execute(String[] args) {
        // This method should not be called directly in client-server mode
        // The RequestProcessor handles add with data
        return CommandResponse.failure("This command requires object data and should be handled by RequestProcessor");
    }

    @Override
    public String getDisplayedName() {
        return "add {element}";
    }
}
