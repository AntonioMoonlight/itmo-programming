import client.AppController;
import client.ConsoleView;
import client.ElementBuilder;
import client.StdInSource;
import server.CollectionManager;
import server.CommandManager;
import server.FileManager;
import server.command.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String envName = "DATA_FILE";
        Path dataFilePath = Paths.get(System.getenv(envName));


        ConsoleView consoleView = new ConsoleView(System.out);
        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(dataFilePath, collectionManager, consoleView);
        ElementBuilder elementBuilder = new ElementBuilder(consoleView, StdInSource.INSTANCE, collectionManager.getIdGenerator());
        CommandManager commandManager = new CommandManager();
        AppController appController = new AppController(StdInSource.INSTANCE, elementBuilder,
                consoleView, commandManager);

        commandManager.register(new Help(consoleView, commandManager));
        commandManager.register(new Exit());
        commandManager.register(new History(commandManager, consoleView));
        commandManager.register(new Show(consoleView, collectionManager));
        commandManager.register(new Add(collectionManager, elementBuilder));
        commandManager.register(new RemoveById(collectionManager));
        commandManager.register(new UpdateById(collectionManager, elementBuilder));
        commandManager.register(new Clear(collectionManager));
        commandManager.register(new Head(collectionManager, consoleView));
        commandManager.register(new RemoveLower(collectionManager, elementBuilder));
        commandManager.register(new SumOfNumberOfParticipants(collectionManager, consoleView));
        commandManager.register(new CountByLabel(collectionManager, consoleView));
        commandManager.register(new FilterLessThanGenre(collectionManager, consoleView));
        commandManager.register(new Info(consoleView, collectionManager));
        commandManager.register(new Save(fileManager));
        commandManager.register(new ExecuteScript(appController));

        List<String> messages = collectionManager.init(fileManager.readCollection());
        messages.forEach(consoleView::println);
        appController.run();
    }
}