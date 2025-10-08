package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.network.Server;
import server.command.*;
import client.ElementBuilder;
import client.StdInSource;
import client.ConsoleView;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Server application main class
 */
public class ServerMain {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Parse command line arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[0]);
                System.err.println("Usage: java ServerMain [port]");
                System.exit(1);
            }
        }

        try {
            // Initialize server components
            String envName = "DATA_FILE";
            String dataFileEnv = System.getenv(envName);
            if (dataFileEnv == null || dataFileEnv.trim().isEmpty()) {
                System.err.println("Environment variable " + envName + " is not set or empty");
                System.exit(1);
            }
            
            Path dataFilePath = Paths.get(dataFileEnv);
            
            ConsoleView consoleView = new ConsoleView(System.out);
            CollectionManager collectionManager = new CollectionManager();
            FileManager fileManager = new FileManager(dataFilePath, collectionManager, consoleView);
            ElementBuilder elementBuilder = new ElementBuilder(consoleView, StdInSource.INSTANCE);
            CommandManager commandManager = new CommandManager();

            // Register server commands
            registerCommands(commandManager, collectionManager, fileManager, elementBuilder, consoleView);

            // Load collection from file
            List<String> messages = collectionManager.init(fileManager.readCollection());
            logger.info("Collection loaded with {} items", collectionManager.getDeque().size());
            messages.forEach(msg -> logger.info("Collection init: {}", msg));

            // Start server
            Server server = new Server(port, commandManager, collectionManager);
            
            // Add shutdown hook to save collection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                server.stop();
                try {
                    fileManager.writeCollection();
                    logger.info("Collection saved to file");
                } catch (Exception e) {
                    logger.error("Error saving collection on shutdown", e);
                }
            }));

            logger.info("Starting server on port {}", port);
            server.start();
            
        } catch (IOException e) {
            logger.error("Failed to start server", e);
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void registerCommands(CommandManager commandManager, CollectionManager collectionManager, 
                                       FileManager fileManager, ElementBuilder elementBuilder, ConsoleView consoleView) {
        
        commandManager.register(new Help(commandManager));
        commandManager.register(new History(commandManager));
        commandManager.register(new Show(collectionManager));
        commandManager.register(new Add(collectionManager));
        commandManager.register(new RemoveById(collectionManager));
        commandManager.register(new UpdateById(collectionManager));
        commandManager.register(new Clear(collectionManager));
        commandManager.register(new Head(collectionManager));
        commandManager.register(new RemoveLower(collectionManager));
        commandManager.register(new SumOfNumberOfParticipants(collectionManager));
        commandManager.register(new CountByLabel(collectionManager));
        commandManager.register(new FilterLessThanGenre(collectionManager));
        commandManager.register(new Info(collectionManager));
        commandManager.register(new Save(fileManager)); // Server-only command
    }
}