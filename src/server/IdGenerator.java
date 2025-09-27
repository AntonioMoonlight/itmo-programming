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

    public void assignNextId(MusicBand band) {
        this.assignId(next(), band);
    }

    public int next() {
        return ++counter;
    }

    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }

}
