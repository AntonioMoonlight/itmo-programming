package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import Controller.ElementBuilder;
import common.MusicBand;

public class RemoveLower extends Command {
    private final CollectionManager collectionManager;
    private final ElementBuilder elementBuilder;

    public RemoveLower(CollectionManager collectionManager, ElementBuilder elementBuilder) {
        super("remove_lower", "Removes all element less than the given one.", 0);
        this.collectionManager = collectionManager;
        this.elementBuilder = elementBuilder;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        MusicBand musicBand = elementBuilder.buildMusicBand();
        collectionManager.getDeque().stream()
                .filter(band -> band.compareTo(musicBand) < 0)
                .toList()
                .forEach(collectionManager::remove);
        return CommandResponse.success();
    }

    @Override
    public String getDisplayedName() {
        return "remove_lower {element}";
    }
}
