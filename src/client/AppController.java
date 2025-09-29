package client;

import server.command.Command;
import server.CommandManager;
import server.CommandResponse;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AppController {

    private InputSource inputSource;
    private final ElementBuilder elementBuilder;
    private final ConsoleView consoleView;
    private final CommandManager commandManager;

    private final Set<String> activeScripts = new HashSet<>();

    private record ParsedCommand(String name, String[] args) {}

    public AppController(InputSource inputSource, ElementBuilder elementBuilder, ConsoleView consoleView, CommandManager commandManager) {
        this.inputSource = inputSource;
        this.elementBuilder = elementBuilder;
        this.consoleView = consoleView;
        this.commandManager = commandManager;
    }

    public void run() {
        if (!inputSource.isInteractive()) elementBuilder.setInputSource(inputSource);
        else consoleView.prompt();

        while (true) {
            Optional<String> optLine = inputSource.nextLine();
            if (optLine.isEmpty()) break;

            String line = optLine.get().trim();
            if (line.isEmpty()) continue;

            proccessLine(line);
            if (inputSource.isInteractive()) consoleView.prompt();

         }
    }

    private void proccessLine(String line) {
        ParsedCommand parsedCommand = parse(line);
        Command command = commandManager.getRegistry().get(parsedCommand.name);
        if (command == null) {
            consoleView.println("Unknown command: " + parsedCommand.name);
            return;
        }
        if ("execute_script".equals(parsedCommand.name)) {
            processScript(command, parsedCommand.args);
        } else {
            processCommand(command, parsedCommand.args);
        }
    }
    private void processScript(Command command, String[] args) {
        command.validateArgCount(args, 1);
        String fileName = args[0];

        if (detectRecursion(fileName)) {
            return;
        }
        try {
            processCommand(command, args);
        } finally {
            activeScripts.remove(fileName);
            elementBuilder.setInputSource(StdInSource.INSTANCE);
        }
    }
    private void processCommand(Command command, String[] args) {
        try {
            CommandResponse response = command.validateAndExecute(args);
            commandManager.getHistory().addFirst(command.getName());
            handleResponse(response);
        } catch (ElementBuilder.NoMoreInputException e) {
            consoleView.println("Input not available.");
        }
    }

    private boolean detectRecursion(String fileName) {
        if (activeScripts.contains(fileName)) {
            consoleView.println("Recursion detected: " + fileName + " is already executing.");
            return true;
        }
        activeScripts.add(fileName);
        return false;
    }

    private ParsedCommand parse(String line) {
        String[] parts = line.trim().split("\\s+", 2);
        String name = parts[0];
        String[] args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];
        return new ParsedCommand(name, args);
    }

    private void handleResponse(CommandResponse response) {
        if (response.message().equals("exit")) {
            consoleView.println("Exiting without saving...");
            System.exit(0);
        }
        if (!response.successFlag()) {
            consoleView.println("Failure: " + response.message());
        }
        if (response.successFlag() && !response.message().equals("Success.")) {
            consoleView.println(response.message());
        }
    }

    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    public InputSource getInputSource() {
        return inputSource;
    }
}
