package server.command;

import server.CollectionManager;
import common.Response;
import common.MusicBand;

public class Add extends Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        super("add", "Adds a new element to the collection from user input.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        return new Response(false, "Add command requires MusicBand data");
    }
    
    @Override
    public Response execute(String[] args, MusicBand musicBand) {
        if (musicBand == null) {
            return new Response(false, "Add command requires MusicBand data");
        }
        
        String msg = collectionManager.add(musicBand);
        return new Response(true, msg);
    }

    @Override
    public String getDisplayedName() {
        return "add {element}";
    }
}
