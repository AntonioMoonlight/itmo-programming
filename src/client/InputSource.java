package client;

import java.util.Optional;

public interface InputSource {
    Optional<String> nextLine();
    boolean isInteractive();
    void close();
}
