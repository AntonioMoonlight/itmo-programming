package server.command;

import server.CollectionManager;
import common.Response;

public class CountByLabel extends Command {
    private final CollectionManager collectionManager;
    
    public CountByLabel(CollectionManager collectionManager) {
        super("count_by_label", "Shows the number of elements with the same label name.", 1);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        String labelName = args[0];
        long count = collectionManager.countByLabel(labelName);
        return new Response(true, "Elements with label '" + labelName + "': " + count);
    }

    @Override
    public String getDisplayedName() {
        return "count_by_label label";
    }
}
