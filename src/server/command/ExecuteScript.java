package server.command;

import server.network.RequestProcessor;
import common.Response;
import common.Request;
import client.ElementBuilder;

import java.util.HashSet;
import java.util.Set;

public class ExecuteScript extends Command {
    private final RequestProcessor requestProcessor;
    private final Set<String> activeScripts = new HashSet<>();

    public ExecuteScript(RequestProcessor requestProcessor) {
        super("execute_script", "Executes script content received from client", 1);
        this.requestProcessor = requestProcessor;
    }

    @Override
    public Response execute(String[] args) throws ElementBuilder.NoMoreInputException {
        return new Response(false, "ExecuteScript command requires script content data");
    }

    // This method will be called when the client sends script content as String data
    public Response executeScript(String[] args, String scriptContent) throws ElementBuilder.NoMoreInputException {
        if (args.length != 1) {
            return new Response(false, "Usage: execute_script <filename>");
        }

        String fileName = args[0];

        // Check for script recursion
        if (isScriptActive(fileName)) {
            return new Response(false, "Script recursion detected: " + fileName);
        }

        try {
            addActiveScript(fileName);

            // Split script content into lines and execute each command
            String[] lines = scriptContent.split("\\r?\\n");
            StringBuilder results = new StringBuilder();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }

                // Parse the command
                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0];
                String[] commandArgs = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];

                // Handle nested execute_script commands
                if ("execute_script".equals(commandName)) {
                    // Nested scripts should have been resolved by the client already
                    // If we see this, it means there's an issue with client-side processing
                    results.append("Warning: execute_script command found in processed script - this should have been resolved by client\n");
                    continue;
                }

                // Create request for this command
                Request request = new Request(commandName, commandArgs);
                Response response = requestProcessor.processRequest(request);

                if (!response.isSuccess()) {
                    results.append("Error executing '").append(line).append("': ").append(response.getMessage()).append("\n");
                } else if (!"Success.".equals(response.getMessage()) && !response.getMessage().isEmpty()) {
                    results.append(response.getMessage()).append("\n");
                }
            }

            String resultMessage = results.length() > 0 ? results.toString().trim() : "Script executed successfully";
            return new Response(true, resultMessage);

        } finally {
            removeActiveScript(fileName);
        }
    }

    private boolean isScriptActive(String fileName) {
        return activeScripts.contains(fileName);
    }

    private void addActiveScript(String fileName) {
        activeScripts.add(fileName);
    }

    private void removeActiveScript(String fileName) {
        activeScripts.remove(fileName);
    }

    @Override
    public String getDisplayedName() {
        return "execute_script filename";
    }
}