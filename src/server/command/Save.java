package server.command;

import common.Response;
import client.ElementBuilder;
import server.FileManager;
import java.io.FileNotFoundException;

public class Save extends Command {
    private final FileManager fileManager;
    public Save(FileManager fileManager) {
        super("save", "Saves the collection to the file", 0);
        this.fileManager = fileManager;
    }

    @Override
    public Response execute(String[] args) throws ElementBuilder.NoMoreInputException {
        try {
            fileManager.writeCollection();
            return new Response(true, "Collection saved successfully.");
        } catch (FileNotFoundException e) {
            return new Response(false, "File not found or not permitted to read.");
        }
    }
}
