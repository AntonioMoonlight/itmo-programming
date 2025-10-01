package common;

import java.io.Serializable;

public class Coordinates implements Serializable {
    public Double getX() {
        return x;
    }

    private final Double x; //Поле не может быть null
    private final int y;
    public Coordinates(Double x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %d)", x, y);
    }
}