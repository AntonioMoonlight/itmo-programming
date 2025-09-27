package Controller.Command;

import Controller.AppController;
import Controller.CommandManager;
import Controller.CommandResponse;
import Controller.ElementBuilder;
import View.ConsoleView;
import View.FileInputSource;
import View.InputSource;

import java.io.FileNotFoundException;

public class ExecuteScript extends Command{
    private final ConsoleView consoleView;
    private final CommandManager commandManager;
    private final ElementBuilder elementBuilder;
    private final AppController appController;
    public ExecuteScript(ConsoleView consoleView, CommandManager commandManager, ElementBuilder elementBuilder, AppController appController) {
        super("execute_script", "Reads and executes script from the given file.", 1);
        this.consoleView = consoleView;
        this.commandManager = commandManager;
        this.elementBuilder = elementBuilder;
        this.appController = appController;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        InputSource prevSource = appController.getInputSource();
        String fileName = args[0];
        try {
            FileInputSource fileInputSource = new FileInputSource(fileName);
            appController.setInputSource(fileInputSource);
            appController.run();
            appController.setInputSource(prevSource);
            return CommandResponse.success();
        } catch (FileNotFoundException e) {
            return CommandResponse.failure("File not found.");
        }
    }

    @Override
    public String getDisplayedName() {
        return "execute_script file_name";
    }
}
