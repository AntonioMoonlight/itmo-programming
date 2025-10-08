package server.command;

import server.CollectionManager;
import server.CommandResponse;
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
    public CommandResponse execute(String[] args) {
        try {
            MusicGenre genre = MusicGenre.valueOf(args[0].toUpperCase());
            List<MusicBand> filtered = collectionManager.filterLessThanGenre(genre);
            
            if (filtered.isEmpty()) {
                return new CommandResponse(true, "No elements found with genre less than " + genre);
            }
            
            return new CommandResponse(true, "Elements with genre less than " + genre + ":");
        } catch (IllegalArgumentException e) {
            return CommandResponse.failure("Invalid genre. Allowed genres: " + MusicGenre.allowed);
        }
    }

    @Override
    public String getDisplayedName() {
        return "filter_less_than_genre genre";
    }
}
