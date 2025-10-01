package client;

import common.MusicBand;

import java.io.PrintStream;
import java.util.ArrayDeque;

public class ConsoleView {
    private final PrintStream out;
    private final String hLine = "+"
            + "-".repeat(6)  + "+"
            + "-".repeat(22) + "+"
            + "-".repeat(17) + "+"
            + "-".repeat(22) + "+"
            + "-".repeat(7)  + "+"
            + "-".repeat(14) + "+"
            + "-".repeat(12) + "+"
            + "-".repeat(22) + "+";

    private final String header = "| ID   | Name                 | Coordinates     | Created              | #Part | Established  | Genre      | Label                |";

    private final String PROMPT = ">";

    public ConsoleView(PrintStream out) {
        this.out = out;
    }
    public void println(String s) {out.println(s);}
    public void print(String s) {out.print(s);}

    public void prompt(String s) {
        println("Enter " + s);
        print(PROMPT);
    }
    public void prompt(){
        print(PROMPT + " ");
    }

    public void printMusicBandsTable(ArrayDeque<MusicBand> deque) {
        println(hLine);
        println(header);
        println(hLine);
        deque.forEach(band -> println(band.toString()));
        println(hLine);
    }

    public void printMusicBandTable(MusicBand band) {
        println(hLine);
        println(header);
        println(hLine);
        println(band.toString());
        println(hLine);
    }


}
