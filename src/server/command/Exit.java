package server.command;

import server.CommandResponse;

public class Exit extends Command {
    public Exit() {
        super("exit", "Terminates the program without saving.", 0);
    }

    @Override
    public CommandResponse execute(String[] args) {
        return new CommandResponse(true, "exit");
    }
}
