package server.command;

import server.CollectionManager;
import server.CommandResponse;
import client.ElementBuilder;
import common.MusicBand;

public class SumOfNumberOfParticipants extends Command {

    private final CollectionManager collectionManager;

    public SumOfNumberOfParticipants(CollectionManager collectionManager) {
        super("sum_of_number_of_participants", "Shows the sum of the number of participants of all bands.",
                0);
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String[] args) throws ElementBuilder.NoMoreInputException {
        int sum = collectionManager.getDeque().stream()
                .mapToInt(MusicBand::getNumberOfParticipants)
                .sum();
        
        return new CommandResponse(true, "Sum of number of participants: " + sum);
    }
}
