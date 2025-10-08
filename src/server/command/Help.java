package server.command;

import server.CommandManager;
import server.CommandResponse;

import java.util.Map;

public class Help extends Command {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        super("help", "Shows information about the commands.", 0);
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
        
        StringBuilder output = new StringBuilder();
        output.append(border).append("\n");
        output.append(header).append("\n");
        output.append(border).append("\n");

        for (Command cmd : registry.values()) {
            output.append(String.format(
                    "| %-" + maxNameLength + "s | %-" + maxDescLength + "s | %-" + argColumnWidth + "d |\n",
                    cmd.getDisplayedName(),
                    cmd.getDescription(),
                    cmd.getVariableNumber()));
        }
        output.append(border);

        return new CommandResponse(true, output.toString());
    }
}
