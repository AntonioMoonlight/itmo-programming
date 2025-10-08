package server.command;

import server.CollectionManager;
import common.Response;

import java.time.format.DateTimeFormatter;

public class Info extends Command {

    private final CollectionManager collectionManager;
    
    public Info(CollectionManager collectionManager) {
        super("info", "Shows information about the collection.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        String formattedDate = collectionManager.getInitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String info = "Information about the collection:" + "\n" +
                "Type: ArrayDeque" + "\n" +
                "Initialization date: " + formattedDate + "\n" +
                "Number of elements: " + collectionManager.getDeque().size();
        
        return new Response(true, info);
    }
}
