package common;

import java.io.Serializable;

public class Label implements Serializable {
    private final String name;
    private final Integer bands; //Поле может быть null
    public Label(String name, Integer bands) {
        this.name = name;
        this.bands = bands;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name,
        bands != null ? bands : "-");
    }
}