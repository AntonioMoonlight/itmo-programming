package server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandManager;
import server.CollectionManager;
import server.command.Command;
import server.command.ExecuteScript;
import common.Request;
import common.Response;
import common.MusicBand;
import client.ElementBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Processes incoming requests and converts them to responses
 */
public class RequestProcessor {
    private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
    
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;

    public RequestProcessor(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    public Response processRequest(Request request) {
        logger.debug("Processing request: {}", request.getCommandName());
        
        try {
            Command command = commandManager.getRegistry().get(request.getCommandName());
            
            if (command == null) {
                return new Response(false, "Unknown command: " + request.getCommandName());
            }

            // Execute command with proper delegation
            Response response;
            if (needsMusicBandData(request.getCommandName()) && request.getData() instanceof MusicBand) {
                // Execute command with MusicBand data for commands that need it
                response = command.validateAndExecute(request.getArguments(), (MusicBand) request.getData());
            } else if ("execute_script".equals(request.getCommandName()) && request.getData() instanceof String) {
                // Handle ExecuteScript with script content as String data
                ExecuteScript executeScript = (ExecuteScript) command;
                response = executeScript.executeScript(request.getArguments(), (String) request.getData());
            } else {
                // Execute command normally
                response = command.validateAndExecute(request.getArguments());
            }
            
            // Add command to history
            if (response.isSuccess()) {
                commandManager.getHistory().addFirst(command.getName());
            }

            // For commands that return collection data, include sorted collection
            if (isCollectionCommand(request.getCommandName()) && response.isSuccess()) {
                List<MusicBand> sortedCollection = collectionManager.getDeque().stream()
                    .sorted((b1, b2) -> {
                        // Sort by coordinates (x, then y)
                        int coordCompare = Double.compare(b1.getCoordinates().getX(), b2.getCoordinates().getX());
                        if (coordCompare != 0) return coordCompare;
                        return Double.compare(b1.getCoordinates().getY(), b2.getCoordinates().getY());
                    })
                    .collect(Collectors.toList());
                
                response = new Response(response.isSuccess(), response.getMessage(), sortedCollection);
            }

            logger.debug("Request processed successfully: {}", request.getCommandName());
            return response;
            
        } catch (ElementBuilder.NoMoreInputException e) {
            logger.warn("Input not available for command: {}", request.getCommandName());
            return new Response(false, "Input not available for this command on server side");
        } catch (Exception e) {
            logger.error("Error processing request: {}", request.getCommandName(), e);
            return new Response(false, "Server error: " + e.getMessage());
        }
    }

    private boolean needsMusicBandData(String commandName) {
        return "add".equals(commandName) || 
               "update_by_id".equals(commandName) || 
               "remove_lower".equals(commandName);
    }

    /**
     * Determines if a command returns collection data that should be sent to client
     */
    private boolean isCollectionCommand(String commandName) {
        return "show".equals(commandName) || 
               "head".equals(commandName) ||
               "filter_less_than_genre".equals(commandName);
    }
}