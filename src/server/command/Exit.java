package server.command;

import client.AppController;
import server.CommandResponse;

public class Exit extends Command {
    private final AppController appController;
    public Exit(AppController appController) {
        super("exit", "Terminates the program without saving.", 0);
        this.appController = appController;
    }

    @Override
    public CommandResponse execute(String[] args) {
        appController.stop();
        return new CommandResponse(true, "Exiting without saving...");
    }
}
