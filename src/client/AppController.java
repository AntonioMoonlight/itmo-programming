package client;

import server.command.Command;
import server.CommandManager;
import server.CommandResponse;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AppController {

    private final ElementBuilder elementBuilder;
    private final ConsoleView consoleView;
    private final CommandManager commandManager;
    private final Set<String> activeScripts = new HashSet<>();
    private boolean isRunning = true;
    private record ParsedCommand(String name, String[] args) {}

    public AppController(ElementBuilder elementBuilder,
                         ConsoleView consoleView, CommandManager commandManager) {
        this.elementBuilder = elementBuilder;
        this.consoleView = consoleView;
        this.commandManager = commandManager;
    }

    public void run(InputSource inputSource) {
        if (!inputSource.isInteractive()) {
            elementBuilder.setInputSource(inputSource);
        }

        while (isRunning) {
            if (inputSource.isInteractive()) consoleView.prompt();

            Optional<String> optLine = inputSource.nextLine();
            if (optLine.isEmpty()) {
                if (!inputSource.isInteractive()) {
                    inputSource = StdInSource.INSTANCE;
                    continue;
                }
                break;
            }

            String line = optLine.get().trim();
            if (!line.isEmpty()) {
                processLine(line);
            }
        }
    }

    public void processLine(String line) {
        ParsedCommand parsedCommand = parse(line);
        Command command = commandManager.getRegistry().get(parsedCommand.name);
        if (command == null) {
            consoleView.println("Unknown command: " + parsedCommand.name);
            return;
        }
        processCommand(command, parsedCommand.args());
    }
    private void processCommand(Command command, String[] args) {
        try {
            CommandResponse response = command.validateAndExecute(args);
            if (!isRunning) return;

            commandManager.getHistory().addFirst(command.getName());
            handleResponse(response);
        } catch (ElementBuilder.NoMoreInputException e) {
            consoleView.println("Input not available.");
        } catch (Exception e) {
            consoleView.println("Execution error.");
        }
    }

    private ParsedCommand parse(String line) {
        String[] parts = line.trim().split("\\s+", 2);
        String name = parts[0];
        String[] args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];
        return new ParsedCommand(name, args);
    }

    private void handleResponse(CommandResponse response) {
        if (!response.successFlag()) {
            consoleView.println("Failure: " + response.message());
        }
        if (response.successFlag() && !"Success.".equals(response.message())) {
            consoleView.println(response.message());
        }
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isScriptActive(String fileName) {
        return activeScripts.contains(fileName);
    }

    public void addActiveScript(String fileName) {
        activeScripts.add(fileName);
    }

    public void removeActiveScript(String fileName) {
        activeScripts.remove(fileName);
    }
}
