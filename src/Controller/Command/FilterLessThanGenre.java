package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import common.MusicGenre;
import View.ConsoleView;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

public class FilterLessThanGenre extends Command {
    private CollectionManager collectionManager;
    private ConsoleView consoleView;

    public FilterLessThanGenre(CollectionManager collectionManager, ConsoleView consoleView) {
        super("filter_less_than_genre", "Shows all elements with field genre less than the given one,",
                1);
        this.collectionManager = collectionManager;
        this.consoleView = consoleView;
    }

    @Override
    public CommandResponse execute(String[] args) {
        try {
            MusicGenre genre = MusicGenre.valueOf(args[0].toUpperCase());
            consoleView.printMusicBandsTable(collectionManager.getDeque().stream()
                    .filter(g -> g.getGenre().compareTo(genre) < 0)
                    .collect(Collectors.toCollection(ArrayDeque::new)));
            return CommandResponse.success();
        } catch (IllegalArgumentException e) {
            return CommandResponse.failure("Invalid genre. Allowed genres: " + MusicGenre.allowed);
        }
    }

    @Override
    public String getDisplayedName() {
        return "filter_less_than_genre genre";
    }
}
