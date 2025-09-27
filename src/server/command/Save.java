package server.command;

import server.CommandResponse;
import client.ElementBuilder;
import server.FileManager;
import client.ConsoleView;

import java.io.FileNotFoundException;

public class Save extends Command {
    private final FileManager fileManager;
    private final ConsoleView consoleView;
    public Save(FileManager fileManager, ConsoleView consoleView) {
        super("save", "Saves the collection to the file", 0);
        this.fileManager = fileManager;
        this.consoleView = consoleView;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        try {
            fileManager.writeCollection();
            return CommandResponse.success();
        } catch (FileNotFoundException e) {
            return CommandResponse.failure("File not found or not permitted to read.");
        }
    }
}
