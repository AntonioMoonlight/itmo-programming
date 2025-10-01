package client;

import server.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        String envName = "DATA_FILE";
        /*
        ConsoleView consoleView = new ConsoleView(System.out);
        ElementBuilder elementBuilder = new ElementBuilder(consoleView, idGenerator, StdInSource.INSTANCE);
        CommandManager commandManager = new CommandManager();
        AppController appController = new AppController(elementBuilder,
                consoleView, commandManager);

        appController.run(StdInSource.INSTANCE);*/
    }
}
