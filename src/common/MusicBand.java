package common;

import client.MusicBandValidator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import static client.MusicBandValidator.*;
import static client.MusicBandValidator.labelNameNotNull;

public class MusicBand implements Comparable<MusicBand>, Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //Поле не может быть null
    private final LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final int numberOfParticipants; //Значение поля должно быть больше 0
    private final Date establishmentDate; //Поле может быть null
    private final MusicGenre genre; //Поле не может быть null
    private final Label label; //Поле не может быть null

    public MusicBand(int id, String name, Coordinates coordinates, LocalDateTime creationDate, int numberOfParticipants,
                     Date establishmentDate, MusicGenre genre, Label label) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.numberOfParticipants = numberOfParticipants;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.label = label;
    }

    public MusicBand(MusicBand other) {
        this.id = other.id;
        this.name = other.name;
        this.coordinates = other.coordinates;
        this.creationDate = other.creationDate;
        this.numberOfParticipants = other.numberOfParticipants;
        this.establishmentDate = other.establishmentDate;
        this.genre = other.genre;
        this.label = other.label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public int compareTo(MusicBand other) {
        return Comparator.comparing(MusicBand::getNumberOfParticipants)
                .thenComparing(MusicBand::getGenre)
                .compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicBand musicBand = (MusicBand) o;
        return id == musicBand.id &&
                numberOfParticipants == musicBand.numberOfParticipants &&
                Objects.equals(name, musicBand.name) &&
                Objects.equals(coordinates, musicBand.coordinates) &&
                Objects.equals(creationDate, musicBand.creationDate) &&
                Objects.equals(establishmentDate, musicBand.establishmentDate) &&
                genre == musicBand.genre &&
                Objects.equals(label, musicBand.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, numberOfParticipants, establishmentDate, genre, label);
    }

    @Override
    public String toString() {
        String formattedCreationDate = creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String estab = establishmentDate != null
                ? new SimpleDateFormat("yyyy-MM-dd").format(establishmentDate)
                : "-";

        return String.format(
                "| %-4d | %-20s | %-15s | %-20s | %-5d | %-12s | %-10s | %-20s |",
                id,
                name,
                coordinates,
                formattedCreationDate,
                numberOfParticipants,
                estab,
                genre,
                label
        );
    }

    public MusicBandValidator.ValidationResult validate() {
        return idPositive()
                .andCollect(coordinatesNotNull())
                .andCollect(xCoordinateNotNull())
                .andCollect(creationDateNotNull())
                .andCollect(numberOfParticipantsPositive())
                .andCollect(genreNotNull())
                .andCollect(labelNotNull())
                .andCollect(labelNameNotNull())
                .apply(this);
    }
}
