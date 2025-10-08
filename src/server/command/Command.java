package server.command;
import common.Response;
import client.ElementBuilder;
import common.MusicBand;

public abstract class Command {
    private final String name;
    private final String description;
    private final int variableNumber;

    protected Command(String name, String description, int variableNumber) {
        this.name = name;
        this.description = description;
        this.variableNumber = variableNumber;
    }

    public abstract Response execute(String[] args) throws ElementBuilder.NoMoreInputException;
    
    // New method for commands that need MusicBand data
    public Response execute(String[] args, MusicBand musicBand) throws ElementBuilder.NoMoreInputException {
        // Default implementation delegates to the basic execute method
        return execute(args);
    }
    
    public Response validateAndExecute(String[] args) throws ElementBuilder.NoMoreInputException {
        Response validationResponse = validateArgCount(args, variableNumber);
        if (!validationResponse.isSuccess()) return validationResponse;
        return execute(args);
    }
    
    public Response validateAndExecute(String[] args, MusicBand musicBand) throws ElementBuilder.NoMoreInputException {
        Response validationResponse = validateArgCount(args, variableNumber);
        if (!validationResponse.isSuccess()) return validationResponse;
        return execute(args, musicBand);
    }
    public Response validateArgCount(String[] args, int variableNumber) {
        if (args.length != variableNumber) {
            return new Response(false,
                    String.format("Command '%s' expects %d argument(s), but got %d.",
                            name, variableNumber, args.length)
            );
        }
        return new Response(true, "");
    }

    public String getName() { return name; }
    public String getDisplayedName() {
        return name;
    }
    public String getDescription() { return description; }

    public int getVariableNumber() {
        return variableNumber;
    }
}
