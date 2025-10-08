package server.command;

import server.CollectionManager;
import common.Response;
import common.MusicBand;

public class UpdateById extends Command {
    private final CollectionManager collectionManager;

    public UpdateById(CollectionManager collectionManager) {
        super("update_by_id", "Updates the element with given ID from user input", 1);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        return new Response(false, "UpdateById command requires MusicBand data");
    }
    
    @Override
    public Response execute(String[] args, MusicBand musicBand) {
        if (musicBand == null) {
            return new Response(false, "UpdateById command requires MusicBand data");
        }
        
        try {
            int id = Integer.parseInt(args[0]);
            
            // Check if element with given ID exists
            if (collectionManager.getDeque().stream().noneMatch(b -> b.getId() == id)) {
                return new Response(false, "An element with given ID does not exist.");
            }
            
            collectionManager.update(id, musicBand);
            return new Response(true, "Element with ID " + id + " updated successfully.");
            
        } catch (NumberFormatException e) {
            return new Response(false, "Invalid ID format");
        }
    }

    @Override
    public String getDisplayedName() {
        return "update_by_id id";
    }
}
