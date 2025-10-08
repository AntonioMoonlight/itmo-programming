package client;

import common.Coordinates;
import common.Label;
import common.MusicBand;
import common.MusicGenre;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ElementBuilder {
    private final ConsoleView consoleView;

    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    private InputSource inputSource;

    public ElementBuilder(ConsoleView consoleView, InputSource inputSource) {
        this.consoleView = consoleView;
        this.inputSource = inputSource;
    }


    public MusicBand buildMusicBand() throws NoMoreInputException {
        // id generates automatically

        String name = buildField(
                "name",
                "Name cannot be empty.",
                Function.identity(),
                s -> !s.isEmpty(),
                true
        );

        Coordinates coordinates = buildCoordinates();

        LocalDateTime creationDate = LocalDateTime.now();

        int numberOfParticipants = Objects.requireNonNull(buildField(
                "number of participants",
                "Number must be positive",
                Integer::parseInt,
                n -> (n > 0),
                true
        ));

        Date establishmentDate = buildField(
                "establishment date",
                "Invalid date format",
                s -> {
                    var formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try {
                        return formatter.parse(s);
                    } catch (ParseException e) {
                        return null;
                    }
                },
                s -> true,
                false
        );

        MusicGenre genre = buildGenre();

        Label label = buildLabel();
        // ID will be generated on server side, use temporary ID 0
        return new MusicBand(0,
                name,
                coordinates,
                creationDate,
                numberOfParticipants,
                establishmentDate,
                genre,
                label);
    }

    private Coordinates buildCoordinates() throws NoMoreInputException {
        Double x = buildField(
                "x coordinate",
                "Invalid double",
                Double::parseDouble,
                s -> true,
                true
        );
        int y = Objects.requireNonNull( buildField(
                "y coordinate",
                "Invalid integer",
                Integer::parseInt,
                s -> true,
                false
        ));
        return new Coordinates(x, y);
    }

    private Label buildLabel() throws NoMoreInputException {
        String name = buildField(
                "Label name with no spaces",
                "Label name cannot be empty.",
                Function.identity(),
                s -> !s.isEmpty(),
                true);

        Integer bands = buildField(
                "bands number",
                "Number of bands must be positive or empty.",
                s -> s.isEmpty() ? null : Integer.parseInt(s),
                b -> b == null || b > 0,
                false);

        return new Label(name, bands);
    }
    private MusicGenre buildGenre() throws NoMoreInputException {
        if (inputSource.isInteractive()) consoleView.println("Allowed genres: " + MusicGenre.allowed);
        return buildField(
                "genre",
                "",
                s -> {
                    try {
                        return MusicGenre.valueOf(s.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        consoleView.println("Invalid genre. Allowed genres: " + MusicGenre.allowed);
                        return null;
                    }
                },
                Objects::nonNull,
                true
        );
    }


    /**
     * Generic field builder method.
     *
     * @param fieldName   The name to display in prompt
     * @param errorMsg    Error message to display if validation fails
     * @param parser      Converts string input to desired type
     * @param validator   Predicate to check validity of parsed value
     * @param notNull     Whether to disallow null / empty input
     * @return            Validated and parsed value
     */
    private  <T> T buildField(
            String fieldName,
            String errorMsg,
            Function<String, T> parser,
            Predicate<T> validator,
            boolean notNull) throws NoMoreInputException {

        while (true) {
            if (inputSource.isInteractive()) consoleView.prompt(fieldName);
            Optional<String> optLine = inputSource.nextLine();
            if (optLine.isEmpty()) {
                if (notNull) { throw new NoMoreInputException(fieldName); }
                else { return null; }
            }

            String line = optLine.get().trim();
            if (notNull && line.isEmpty()) {
                consoleView.println(String.format("Field %s cannot be empty.", fieldName));
                continue;
            }

            try {
                T value = parser.apply(line);
                if (!validator.test(value)) {
                    consoleView.println(errorMsg);
                    continue;
                }
                return value;
            } catch (IllegalArgumentException e) {
                Throwable cause = e.getCause() == null ? e : e.getCause();
                consoleView.println(String.format("Invalid value for %s: %s", fieldName, cause.getMessage()));
            }
        }
    }

    public static class NoMoreInputException extends Exception {
        public NoMoreInputException(String fieldName) {
            super("No more input available for field: " + fieldName);
        }
    }
}
