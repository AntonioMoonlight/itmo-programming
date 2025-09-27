package Model;

public class Coordinates {
    public Double getX() {
        return x;
    }

    private Double x; //Поле не может быть null
    private int y;
    public Coordinates(Double x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %d)", x, y);
    }
}