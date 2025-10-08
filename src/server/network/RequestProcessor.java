package server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandManager;
import server.CollectionManager;
import server.CommandResponse;
import server.command.Command;
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

            // Handle commands that need MusicBand data
            CommandResponse commandResponse;
            if (needsMusicBandData(request.getCommandName()) && request.getData() instanceof MusicBand) {
                commandResponse = executeCommandWithData(command, request);
            } else {
                commandResponse = command.validateAndExecute(request.getArguments());
            }
            
            // Add command to history
            if (commandResponse.successFlag()) {
                commandManager.getHistory().addFirst(command.getName());
            }

            // Convert CommandResponse to Response
            Response response;
            
            // For commands that return collection data, include sorted collection
            if (isCollectionCommand(request.getCommandName()) && commandResponse.successFlag()) {
                List<MusicBand> sortedCollection = collectionManager.getDeque().stream()
                    .sorted((b1, b2) -> {
                        // Sort by coordinates (x, then y)
                        int coordCompare = Double.compare(b1.getCoordinates().getX(), b2.getCoordinates().getX());
                        if (coordCompare != 0) return coordCompare;
                        return Double.compare(b1.getCoordinates().getY(), b2.getCoordinates().getY());
                    })
                    .collect(Collectors.toList());
                
                response = new Response(commandResponse.successFlag(), commandResponse.message(), sortedCollection);
            } else {
                response = new Response(commandResponse.successFlag(), commandResponse.message());
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

    private CommandResponse executeCommandWithData(Command command, Request request) throws ElementBuilder.NoMoreInputException {
        MusicBand musicBand = (MusicBand) request.getData();
        
        // For different commands that need MusicBand data, handle appropriately
        if ("add".equals(command.getName())) {
            String msg = collectionManager.add(musicBand);
            return new CommandResponse(true, msg);
        } else if ("update_by_id".equals(command.getName())) {
            if (request.getArguments().length != 1) {
                return CommandResponse.failure("update_by_id requires exactly one argument (id)");
            }
            try {
                int id = Integer.parseInt(request.getArguments()[0]);
                
                // Check if element with given ID exists
                if (collectionManager.getDeque().stream().noneMatch(b -> b.getId() == id)) {
                    return CommandResponse.failure("An element with given ID does not exist.");
                }
                
                collectionManager.update(id, musicBand);
                return new CommandResponse(true, "Element with ID " + id + " updated successfully.");
            } catch (NumberFormatException e) {
                return CommandResponse.failure("Invalid ID format");
            }
        } else if ("remove_lower".equals(command.getName())) {
            long removedCount = collectionManager.removeLower(musicBand);
            return new CommandResponse(true, "Removed " + removedCount + " elements lower than the specified one.");
        }
        
        // Fallback to normal execution
        return command.validateAndExecute(request.getArguments());
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