package Controller.Command;

import Controller.CollectionManager;
import Controller.CommandResponse;
import Controller.ElementBuilder;
import common.MusicBand;
import View.ConsoleView;

public class SumOfNumberOfParticipants extends Command {

    private final CollectionManager collectionManager;
    private final ConsoleView consoleView;

    public SumOfNumberOfParticipants(CollectionManager collectionManager, ConsoleView consoleView) {
        super("sum_of_number_of_participants", "Shows the sum of the number of participants of all bands.",
                0);
        this.collectionManager = collectionManager;
        this.consoleView = consoleView;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        consoleView.println(String.valueOf(
                (collectionManager.getDeque().stream()
                        .mapToInt(MusicBand::getNumberOfParticipants)
                        .sum())));
        return CommandResponse.success();
    }
}
