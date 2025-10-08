package server.command;

import common.Response;

public class Exit extends Command {
    public Exit() {
        super("exit", "Terminates the client session.", 0);
    }

    @Override
    public Response execute(String[] args) {
        return new Response(true, "Exiting...");
    }
}
