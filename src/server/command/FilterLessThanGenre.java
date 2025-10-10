package server.command;

import server.CollectionManager;
import common.Response;
import common.MusicGenre;
import common.MusicBand;

import java.util.List;

public class FilterLessThanGenre extends Command {
    private final CollectionManager collectionManager;

    public FilterLessThanGenre(CollectionManager collectionManager) {
        super("filter_less_than_genre", "Shows all elements with field genre less than the given one,",
                1);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String[] args) {
        try {
            MusicGenre genre = MusicGenre.valueOf(args[0].toUpperCase());
            List<MusicBand> filtered = collectionManager.filterLessThanGenre(genre);
            
            if (filtered.isEmpty()) {
                return new Response(true, "No elements found with genre less than " + genre);
            }
            
            return new Response(true, "Elements with genre less than " + genre + ":", filtered);
        } catch (IllegalArgumentException e) {
            return new Response(false, "Invalid genre. Allowed genres: " + MusicGenre.allowed);
        }
    }

    @Override
    public String getDisplayedName() {
        return "filter_less_than_genre genre";
    }
}
