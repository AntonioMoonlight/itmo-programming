package client;

import common.MusicBand;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static client.MusicBandValidator.*;
public interface MusicBandValidator extends Function<MusicBand, ValidationResult> {

    default MusicBandValidator andCollect(MusicBandValidator other) {
        return t -> {
            ValidationResult result = this.apply(t);
            ValidationResult next = other.apply(t);
            if (result.isValid() && next.isValid()) {
                return ValidationResult.success();
            }
            String combined = Stream.of(result, next)
                    .filter(r -> !r.isValid())
                    .map(ValidationResult::getMessage)
                    .collect(Collectors.joining(", "));
            return ValidationResult.failure(combined);
        };
    }

    static MusicBandValidator idPositive() {
        return band -> band.getId() > 0
                ? ValidationResult.success()
                : ValidationResult.failure("Id must be positive");
    }

    static MusicBandValidator coordinatesNotNull() {
        return band -> band.getCoordinates() != null
                ? ValidationResult.success()
                : ValidationResult.failure("Coordinates must not be null");
    }

    static MusicBandValidator xCoordinateNotNull() {
        return band -> band.getCoordinates().getX() != null
                ? ValidationResult.success()
                : ValidationResult.failure("X coordinate must not be null");
    }

    static MusicBandValidator creationDateNotNull() {
        return band -> band.getCreationDate() != null
                ? ValidationResult.success()
                : ValidationResult.failure("Creation date must not be null");
    }

    static MusicBandValidator numberOfParticipantsPositive() {
        return band -> band.getNumberOfParticipants() > 0
                ? ValidationResult.success()
                : ValidationResult.failure("Number of participants must be positive");
    }

    static MusicBandValidator genreNotNull() {
        return band -> band.getGenre() != null
                ? ValidationResult.success()
                : ValidationResult.failure("Genre must not be null");
    }

    static MusicBandValidator labelNotNull() {
        return band -> band.getLabel() != null
                ? ValidationResult.success()
                : ValidationResult.failure("Label must not be null");
    }

    static MusicBandValidator labelNameNotNull() {
        return band -> band.getLabel().getName() != null
                ? ValidationResult.success()
                : ValidationResult.failure("Label name must not be null");
    }




    record ValidationResult(boolean valid, String message) {
        public static ValidationResult success() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return valid
                    ? "Validation succeeded."
                    : "Validation failed: " + message;
        }
    }
}
