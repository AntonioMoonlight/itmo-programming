package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import Controller.ElementBuilder;
import Model.MusicBand;
import View.ConsoleView;

public class Add extends Command {

    private final CollectionManager collectionManager;
    private final ElementBuilder elementBuilder;

    public Add(CollectionManager collectionManager, ElementBuilder elementBuilder) {
        super("add", "Adds a new element to the collection from user input.", 0);
        this.collectionManager = collectionManager;
        this.elementBuilder = elementBuilder;
    }


    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException, IllegalStateException {
            MusicBand musicBand = elementBuilder.buildMusicBand();
            String msg = collectionManager.add(musicBand);
            return new CommandResponse(true,msg);
    }

    @Override
    public String getDisplayedName() {
        return "add {element}";
    }
}
