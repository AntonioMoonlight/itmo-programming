package server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandManager;
import server.command.Command;
import server.command.ExecuteScript;
import common.Request;
import common.Response;
import common.MusicBand;
import client.ElementBuilder;

/**
 * Processes incoming requests and converts them to responses
 */
public class RequestProcessor {
    private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
    
    private final CommandManager commandManager;

    public RequestProcessor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response processRequest(Request request) {
        logger.debug("Processing request: {}", request.getCommandName());
        
        try {
            Command command = commandManager.getRegistry().get(request.getCommandName());
            
            if (command == null) {
                return new Response(false, "Unknown command: " + request.getCommandName());
            }

            Response response;
            if (needsMusicBandData(request.getCommandName()) && request.getData() instanceof MusicBand) {

                response = command.validateAndExecute(request.getArguments(), (MusicBand) request.getData());
            } else if ("execute_script".equals(request.getCommandName()) && request.getData() instanceof String) {

                ExecuteScript executeScript = (ExecuteScript) command;
                response = executeScript.executeScript(request.getArguments(), (String) request.getData());
            } else {

                response = command.validateAndExecute(request.getArguments());
            }
            
            if (response.isSuccess()) {
                commandManager.getHistory().addFirst(command.getName());
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
}