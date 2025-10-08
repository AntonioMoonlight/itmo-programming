package client;

import client.network.Client;
import common.Request;
import common.Response;
import common.MusicBand;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Network-enabled application controller that communicates with remote server
 */
public class NetworkAppController {
    private final ElementBuilder elementBuilder;
    private final ConsoleView consoleView;
    private final Client networkClient;
    private final Set<String> activeScripts = new HashSet<>();
    private boolean isRunning = true;
    
    // Client-side command blacklist (commands that should not be sent to server)
    private final Set<String> clientOnlyCommands = Set.of();
    private final Set<String> serverOnlyCommands = Set.of("save");

    private record ParsedCommand(String name, String[] args) {}

    public NetworkAppController(ElementBuilder elementBuilder, ConsoleView consoleView, Client networkClient) {
        this.elementBuilder = elementBuilder;
        this.consoleView = consoleView;
        this.networkClient = networkClient;
    }

    public void run(InputSource inputSource) {
        if (!inputSource.isInteractive()) {
            elementBuilder.setInputSource(inputSource);
        }

        // Try to connect to server
        try {
            networkClient.connect();
        } catch (IOException e) {
            consoleView.println("Failed to connect to server: " + e.getMessage());
            consoleView.println("Please check if server is running and try again.");
            return;
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
        
        // Disconnect when done
        networkClient.disconnect();
    }

    public void processLine(String line) {
        ParsedCommand parsedCommand = parse(line);
        String commandName = parsedCommand.name();
        
        // Handle client-side commands
        if (clientOnlyCommands.contains(commandName)) {
            handleClientCommand(parsedCommand);
            return;
        }
        
        // Handle exit command
        if ("exit".equals(commandName)) {
            stop();
            return;
        }
        
        // Check for server-only commands
        if (serverOnlyCommands.contains(commandName)) {
            consoleView.println("Command '" + commandName + "' is not available on client side.");
            return;
        }

        // Send command to server
        processServerCommand(parsedCommand);
    }

    private void handleClientCommand(ParsedCommand parsedCommand) {
        // No more client-only commands since execute_script is now handled by server
        consoleView.println("Unknown client command: " + parsedCommand.name());
    }

    private void processServerCommand(ParsedCommand parsedCommand) {
        try {
            Request request;
            
            // Special handling for execute_script - send file content
            if ("execute_script".equals(parsedCommand.name())) {
                if (parsedCommand.args().length != 1) {
                    consoleView.println("Usage: execute_script <filename>");
                    return;
                }
                
                String fileName = parsedCommand.args()[0];
                
                // Check for recursion
                if (isScriptActive(fileName)) {
                    consoleView.println("Script recursion detected: " + fileName);
                    return;
                }
                
                try {
                    addActiveScript(fileName);
                    
                    // Read the script file content with nested script resolution
                    String scriptContent = readScriptWithNested(fileName);
                    
                    // Send script content to server
                    request = new Request(parsedCommand.name(), parsedCommand.args(), scriptContent);
                    
                } catch (Exception e) {
                    consoleView.println("Error reading script file: " + e.getMessage());
                    return;
                } finally {
                    removeActiveScript(fileName);
                }
                
            } else if (needsObjectData(parsedCommand.name())) {
                // For commands that need object data (like add, update), get the object first
                MusicBand musicBand = elementBuilder.buildMusicBand();
                request = new Request(parsedCommand.name(), parsedCommand.args(), musicBand);
            } else {
                request = new Request(parsedCommand.name(), parsedCommand.args());
            }
            
            // Send request with retry logic for server unavailability
            Response response = networkClient.sendRequestWithRetry(request, 3, 1000);
            handleResponse(response);
            
        } catch (ElementBuilder.NoMoreInputException e) {
            consoleView.println("Input not available.");
        } catch (IOException e) {
            consoleView.println("Communication error: " + e.getMessage());
            consoleView.println("Server might be temporarily unavailable.");
        } catch (Exception e) {
            consoleView.println("Error: " + e.getMessage());
        }
    }

    private boolean needsObjectData(String commandName) {
        return "add".equals(commandName) || 
               "update_by_id".equals(commandName) || 
               "remove_lower".equals(commandName);
    }

    private ParsedCommand parse(String line) {
        String[] parts = line.trim().split("\\s+", 2);
        String name = parts[0];
        String[] args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];
        return new ParsedCommand(name, args);
    }

    private void handleResponse(Response response) {
        if (!response.isSuccess()) {
            consoleView.println("Failure: " + response.getMessage());
        } else {
            if (!"Success.".equals(response.getMessage())) {
                consoleView.println(response.getMessage());
            }
            
            // Display collection data if present
            if (response.getData() != null && !response.getData().isEmpty()) {
                displayCollection(response.getData());
            }
        }
    }

    private void displayCollection(java.util.Collection<MusicBand> collection) {
        if (collection.isEmpty()) {
            consoleView.println("Collection is empty.");
            return;
        }
        
        // Display header
        consoleView.println(String.format(
                "| %-4s | %-20s | %-15s | %-20s | %-5s | %-12s | %-10s | %-20s |",
                "ID", "Name", "Coordinates", "Creation Date", "Parts", "Est. Date", "Genre", "Label"
        ));
        consoleView.println("|" + "-".repeat(6) + "|" + "-".repeat(22) + "|" + "-".repeat(17) + "|" + 
                          "-".repeat(22) + "|" + "-".repeat(7) + "|" + "-".repeat(14) + "|" + 
                          "-".repeat(12) + "|" + "-".repeat(22) + "|");
        
        // Display each music band
        for (MusicBand band : collection) {
            consoleView.println(band.toString());
        }
    }

    /**
     * Recursively reads a script file and resolves all nested execute_script commands
     * by replacing them with the content of the referenced script files
     */
    private String readScriptWithNested(String fileName) throws Exception {
        if (isScriptActive(fileName)) {
            throw new Exception("Script recursion detected: " + fileName);
        }
        
        addActiveScript(fileName);
        try {
            FileInputSource fileSource = new FileInputSource(fileName);
            StringBuilder result = new StringBuilder();
            
            while (true) {
                var optLine = fileSource.nextLine();
                if (optLine.isEmpty()) break;
                
                String line = optLine.get().trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    result.append(line).append("\n");
                    continue;
                }
                
                // Check if this line is an execute_script command
                String[] parts = line.split("\\s+", 2);
                if ("execute_script".equals(parts[0]) && parts.length == 2) {
                    String nestedFileName = parts[1];
                    
                    // Add comment to show where nested script starts
                    result.append("# BEGIN NESTED SCRIPT: ").append(nestedFileName).append("\n");
                    
                    // Recursively read the nested script
                    String nestedContent = readScriptWithNested(nestedFileName);
                    result.append(nestedContent);
                    
                    // Add comment to show where nested script ends  
                    result.append("# END NESTED SCRIPT: ").append(nestedFileName).append("\n");
                } else {
                    // Regular command, just add it
                    result.append(line).append("\n");
                }
            }
            
            return result.toString();
            
        } finally {
            removeActiveScript(fileName);
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