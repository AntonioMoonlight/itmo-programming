package Controller.Command;

import Controller.CommandManager;
import Controller.CommandResponse;
import View.ConsoleView;

import java.util.Map;

public class Help extends Command {
    private final CommandManager commandManager;

    private final ConsoleView consoleView;

    public Help(ConsoleView consoleView, CommandManager commandManager) {
        super("help", "Shows information about the commands.", 0);
        this.consoleView = consoleView;
        this.commandManager = commandManager;
    }

    public CommandResponse execute(String[] args) {
        Map<String, Command> registry = commandManager.getRegistry();

        int maxNameLength = registry.values().stream()
                .map(Command::getDisplayedName)
                .mapToInt(String::length)
                .max()
                .orElse(10);

        int maxDescLength = registry.values().stream()
                .map(Command::getDescription)
                .mapToInt(String::length)
                .max().orElse(10);

        int argColumnWidth = "Args".length();

        String border =
                "+" + "-".repeat(maxNameLength + 2) +
                        "+" + "-".repeat(maxDescLength + 2) +
                        "+" + "-".repeat(argColumnWidth + 2) +
                        "+";

        String header = String.format(
                "| %-" + maxNameLength + "s | %-" + maxDescLength + "s | %-" + argColumnWidth + "s |",
                "Command", "Description", "Args");
        consoleView.println(border);
        consoleView.println(header);
        consoleView.println(border);

        for (Command cmd : registry.values()) {
            consoleView.println(String.format(
                    "| %-" + maxNameLength + "s | %-" + maxDescLength + "s | %-" + argColumnWidth + "d |",
                    cmd.getDisplayedName(),
                    cmd.getDescription(),
                    cmd.getVariableNumber()));
        }
        consoleView.println(border);

        return CommandResponse.success();
    }

}
