package server.command;

import server.CollectionManager;
import common.Response;
import common.MusicBand;

public class RemoveLower extends Command {
    private final CollectionManager collectionManager;

    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "Removes all element less than the given one.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        return new Response(false, "RemoveLower command requires MusicBand data");
    }
    
    @Override
    public Response execute(String[] args, MusicBand musicBand) {
        if (musicBand == null) {
            return new Response(false, "RemoveLower command requires MusicBand data");
        }
        
        long removedCount = collectionManager.removeLower(musicBand);
        return new Response(true, "Removed " + removedCount + " elements lower than the specified one.");
    }

    @Override
    public String getDisplayedName() {
        return "remove_lower {element}";
    }
}
