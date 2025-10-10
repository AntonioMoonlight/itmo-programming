package server.command;

import server.CollectionManager;
import common.Response;
import common.MusicBand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "Shows all elements of the collection.", 0);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        if (collectionManager.getDeque().isEmpty()) {
            return new Response(true, "Collection is empty.");
        }
        
        // Sort collection by coordinates (x, then y) as required
        List<MusicBand> sortedCollection = collectionManager.getDeque().stream()
            .sorted(Comparator.comparingDouble(
                    (MusicBand b) -> b.getCoordinates().getX())
                    .thenComparingInt(b -> b.getCoordinates().getY()))
                .collect(Collectors.toList());
        
        return new Response(true, "Collection contents:", sortedCollection);
    }
}
