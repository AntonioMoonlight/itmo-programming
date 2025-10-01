package server.command;

import client.AppController;
import server.CommandResponse;
import client.ElementBuilder;
import client.FileInputSource;
import client.InputSource;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class ExecuteScript extends Command{
    private final AppController appController;
    public ExecuteScript(AppController appController) {
        super("execute_script", "Reads and executes script from the given file.", 1);
        this.appController = appController;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        String fileName = args[0];

        if (appController.isScriptActive(fileName)) {
            return new CommandResponse(false,
                    "Detected recursion: script '" + fileName + "' is already executing.");
        }

        try {
            appController.addActiveScript(fileName);
            Path path = Paths.get(fileName).toAbsolutePath();
            InputSource scriptSource = new FileInputSource(path.toString());

            while (true) {
                Optional<String> optLine = scriptSource.nextLine();
                if (optLine.isEmpty()) break;
                appController.processLine(optLine.get());
            }

        } catch (FileNotFoundException e) {
            return new CommandResponse(false, "Файл скрипта не найден: " + fileName);
        } finally {
            appController.removeActiveScript(fileName);
        }

        return CommandResponse.success();
    }

    @Override
    public String getDisplayedName() {
        return "execute_script file_name";
    }
}
