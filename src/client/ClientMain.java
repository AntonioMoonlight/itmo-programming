package client;

import client.network.Client;

/**
 * Client application main class
 */
public class ClientMain {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        String serverHost = DEFAULT_HOST;
        int serverPort = DEFAULT_PORT;
        
        // Parse command line arguments
        if (args.length >= 1) {
            serverHost = args[0];
        }
        if (args.length >= 2) {
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[1]);
                System.err.println("Usage: java ClientMain [host] [port]");
                System.exit(1);
            }
        }

        System.out.println("Music Band Collection Client");
        System.out.println("Connecting to server at " + serverHost + ":" + serverPort);
        
        try {
            // Initialize client components
            ConsoleView consoleView = new ConsoleView(System.out);
            Client networkClient = new Client(serverHost, serverPort);
            ElementBuilder elementBuilder = new ElementBuilder(consoleView, StdInSource.INSTANCE);
            
            NetworkAppController appController = new NetworkAppController(elementBuilder, consoleView, networkClient);
            
            // Start client
            appController.run(StdInSource.INSTANCE);
            
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}