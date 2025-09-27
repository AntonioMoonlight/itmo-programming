package client;

import java.util.Optional;
import java.util.Scanner;

public class StdInSource implements InputSource {
    public static final StdInSource INSTANCE = new StdInSource();

    private final Scanner scanner = new Scanner(System.in);
    @Override
    public Optional<String> nextLine() {
        if (scanner.hasNextLine()) {
            return Optional.of(scanner.nextLine());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public void close() {}
}
