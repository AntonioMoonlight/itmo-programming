package server.command;

import server.CollectionManager;
import server.CommandResponse;
import client.ElementBuilder;
import common.MusicBand;

public class UpdateById extends  Command {
    private final CollectionManager collectionManager;
    private final ElementBuilder elementBuilder;

    public UpdateById(CollectionManager collectionManager, ElementBuilder elementBuilder) {
        super("update_id", "Updates the element with given ID from user input", 1);
        this.collectionManager = collectionManager;
        this.elementBuilder = elementBuilder;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        try {
            int id = Integer.parseInt(args[0]);
            if (collectionManager.getDeque().stream().noneMatch(b -> b.getId() == id)) {
                return CommandResponse.failure("An element with given ID does not exist.");
            } else {
                MusicBand newBand = elementBuilder.buildMusicBand();
                collectionManager.update(id, newBand);
                return CommandResponse.success();
            }
        } catch (NumberFormatException e) {
            return CommandResponse.failure(e.getMessage());
        }
    }

    @Override
    public String getDisplayedName() {
        return "update_id id";
    }
}
