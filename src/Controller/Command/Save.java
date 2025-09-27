package Controller.Command;

import Controller.CommandResponse;
import Controller.ElementBuilder;
import Controller.FileManager;
import View.ConsoleView;

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
