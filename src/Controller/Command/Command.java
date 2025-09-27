package Controller.Command;
import Controller.CommandResponse;
import Controller.ElementBuilder;

public abstract class Command {
    private final String name;
    private final String description;
    private final int variableNumber;

    protected Command(String name, String description, int variableNumber) {
        this.name = name;
        this.description = description;
        this.variableNumber = variableNumber;
    }

    public abstract CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException;
    public CommandResponse validateAndExecute(String[] args) throws ElementBuilder.NoMoreInputException {
        CommandResponse validationResponse = validateArgCount(args, variableNumber);
        if (!validationResponse.successFlag()) return validationResponse;
        return execute(args);
    }
    public CommandResponse validateArgCount(String[] args, int variableNumber) {
        if (args.length != variableNumber) {
            return CommandResponse.failure(
                    String.format("Command '%s' expects %d argument(s), but got %d.",
                            name, variableNumber, args.length)
            );
        }
        return CommandResponse.success();
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
