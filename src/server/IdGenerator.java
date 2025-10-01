package server;

import common.MusicBand;

public class IdGenerator {

    private int counter;

    public IdGenerator(int counter) {
        this.counter = counter;
    }

    public void assignId(int id, MusicBand band) {
        band.setId(id);
    }

    public int next() {
        return ++counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
