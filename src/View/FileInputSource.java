package View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

public class FileInputSource implements InputSource {

    private final Scanner scanner;
    public FileInputSource(String fileName) throws FileNotFoundException {
        this.scanner = new Scanner(new File(fileName));
    }

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
        return false;
    }

    @Override
    public void close() { scanner.close(); }
}
